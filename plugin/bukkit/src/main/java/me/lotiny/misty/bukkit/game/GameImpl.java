package me.lotiny.misty.bukkit.game;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import io.fairyproject.bukkit.visual.VisualBlockService;
import io.fairyproject.container.Autowired;
import io.fairyproject.mc.scheduler.MCSchedulers;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.event.PlayerScatterEvent;
import me.lotiny.misty.api.event.UHCStartEvent;
import me.lotiny.misty.api.event.UHCWinEvent;
import me.lotiny.misty.api.game.Game;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameSetting;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.api.profile.stats.StatType;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.api.team.TeamManager;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.impl.MainConfig;
import me.lotiny.misty.bukkit.game.listeners.*;
import me.lotiny.misty.bukkit.hook.PluginHookManager;
import me.lotiny.misty.bukkit.listener.PlayerListener;
import me.lotiny.misty.bukkit.manager.WorldManager;
import me.lotiny.misty.bukkit.manager.border.visual.VisualBorderGenerator;
import me.lotiny.misty.bukkit.storage.StorageRegistry;
import me.lotiny.misty.bukkit.task.BorderTask;
import me.lotiny.misty.bukkit.task.GameTask;
import me.lotiny.misty.bukkit.task.RebootTask;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.ReflectionUtils;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import me.lotiny.misty.bukkit.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GameImpl implements Game {

    @Autowired
    private static GameManager gameManager;
    @Autowired
    private static WorldManager worldManager;
    @Autowired
    private static ScenarioManager scenarioManager;
    @Autowired
    private static TeamManager teamManager;
    @Autowired
    private static PluginHookManager pluginHookManager;
    @Autowired
    private static StorageRegistry storageRegistry;
    @Autowired
    private static VisualBlockService visualBlockService;

    @Getter
    private final GameSetting setting;

    @Override
    public List<Listener> listener() {
        return List.of(
                new CombatListener(),
                new CombatLoggerListener(),
                new ConnectListener(),
                new GameListener(),
                new PlayerDeathListener()
        );
    }

    @Override
    public void start() {
        registerListeners();
        HandlerList.unregisterAll(PlayerListener.get());

        GameRegistry registry = gameManager.getRegistry();
        int borderSize = gameManager.getGame().getSetting().getBorderSize();
        pluginHookManager.getChunkLoader().setSize(registry.getUhcWorld(), borderSize);
        pluginHookManager.getChunkLoader().setSize(registry.getNetherWorld(), borderSize / worldManager.getNetherScale());

        PlayerUtils.playSound(XSound.ENTITY_EXPERIENCE_ORB_PICKUP);

        scenarioManager.getScenariosToEnable().forEach(name -> {
            Scenario scenario = scenarioManager.getScenario(name);
            scenarioManager.enable(scenario, gameManager, Bukkit.getConsoleSender(), false);
        });

        registry.getAlivePlayers().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> {
                    UUID uuid = player.getUniqueId();
                    UHCUtils.giveStarterItem(player);
                    MCSchedulers.getGlobalScheduler().schedule(() -> Bukkit.getPluginManager().callEvent(new PlayerScatterEvent(player, false)));

                    storageRegistry.getProfile(uuid).getStats(StatType.GAME_PLAYED).increase();
                });

        MCSchedulers.getGlobalScheduler().schedule(() -> {
            World gameWorld = Bukkit.getWorld(registry.getUhcWorld());
            if (gameWorld != null) {
                worldManager.clearEntities(gameWorld);
                ReflectionUtils.get().setGameRule(gameWorld, "doDaylightCycle", true);
                gameWorld.setTime(0);
            }
        });

        MainConfig config = Config.getMainConfig();
        if (!scenarioManager.isEnabled("Custom Craft") && config.getHealing().getGoldenHead().isEnabled()) {
            gameManager.addGoldenHeadRecipe();
        }

        GameTask gameTask = new GameTask();
        registry.setGameTask(gameTask);
        gameTask.run(true, 20L);

        BorderTask borderTask = new BorderTask();
        registry.setBorderTask(borderTask);
        borderTask.run(true, 20L);

        registry.setState(GameState.INGAME);

        if (config.getBorder().getVisualBorder() != XMaterial.AIR) {
            visualBlockService.registerGenerator(new VisualBorderGenerator());
        }

        Bukkit.getServer().getPluginManager().callEvent(new UHCStartEvent());
    }

    @Override
    public void stop() {
        unregisterListeners();

        GameRegistry registry = gameManager.getRegistry();
        Team winningTeam = teamManager.getTeams().values().stream().findFirst().orElseThrow();
        List<UUID> winners = winningTeam.getMembers(false);
        registry.setState(GameState.ENDING);

        winners.forEach(uuid -> {
            Profile profile = storageRegistry.getProfile(uuid);
            profile.getStats(StatType.WINS).increase();

            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.getWorld().strikeLightningEffect(player.getLocation());
            }
        });

        String message = "&aCongratulations to &b" +
                winners.stream()
                        .map(uuid -> storageRegistry.getProfile(uuid).getName())
                        .collect(Collectors.joining(", ")) +
                "&a for winning this UHC game!";
        Utilities.broadcast(message);

        RebootTask rebootTask = new RebootTask();
        registry.setRebootTask(rebootTask);
        rebootTask.run(true, 20L);

        Bukkit.getPluginManager().callEvent(new UHCWinEvent(winners));
    }
}
