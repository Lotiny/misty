package me.lotiny.misty.bukkit.task;

import com.cryptomorin.xseries.XSound;
import io.fairyproject.bukkit.util.Players;
import io.fairyproject.container.Autowired;
import io.fairyproject.mc.scheduler.MCSchedulers;
import lombok.Getter;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.task.AbstractScheduleTask;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.api.team.TeamManager;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.impl.MainConfig;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.manager.PracticeManager;
import me.lotiny.misty.bukkit.manager.WorldManager;
import me.lotiny.misty.bukkit.manager.border.BorderManager;
import me.lotiny.misty.bukkit.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class StartTask extends AbstractScheduleTask {

    @Autowired
    private static GameManager gameManager;
    @Autowired
    private static PracticeManager practiceManager;
    @Autowired
    private static WorldManager worldManager;
    @Autowired
    private static BorderManager borderManager;
    @Autowired
    private static ScenarioManager scenarioManager;
    @Autowired
    private static TeamManager teamManager;

    private final boolean force;
    private final String clearChatString;

    public StartTask(int seconds, boolean force) {
        setSeconds(seconds + 1);
        this.force = force;
        this.clearChatString = " \n".repeat(100);
    }

    @Override
    public Runnable tick() {
        return () -> {
            decrementSeconds();
            int currentSeconds = getSeconds();

            MainConfig config = Config.getMainConfig();
            MainConfig.AutoStart autoStart = config.getAutoStart();

            if (autoStart.isEnabled() && !force) {
                if (autoStart.getMinPlayers() > gameManager.getRegistry().getPlayers().size()) {
                    if (autoStart.isCanceled()) {
                        cancelCountdown(autoStart);
                        return;
                    }
                }
            }

            if (currentSeconds <= 0) {
                cancel();
                return;
            }

            if (isImportanceSeconds(currentSeconds)) {
                if (currentSeconds == (config.getWhitelistOffBefore() * 60)) {
                    if (gameManager.getRegistry().isWhitelist()) {
                        gameManager.getRegistry().setWhitelist(Bukkit.getConsoleSender(), false);
                    }
                }

                if (currentSeconds == 60 && practiceManager.isOpened()) {
                    practiceManager.setOpened(false, Bukkit.getConsoleSender());
                }

                Utilities.broadcast(Message.SCATTER_TIME.toString()
                        .replace("<time>", TimeFormatUtils.formatTimeUnit(currentSeconds)));
            }
        };
    }

    @Override
    public void onCancel() {
        handleScatter();
    }

    private void cancelCountdown(MainConfig.AutoStart autoStart) {
        remove();
        gameManager.getRegistry().setStartTask(null);
        PlayerUtils.playSound(XSound.UI_BUTTON_CLICK);
        Utilities.broadcast("&cThe countdown time has been cancelled.");

        if (autoStart.getAnnounce().isEnabled()) {
            new AnnounceTask().run(true, 20L);
        }
    }

    private void handleScatter() {
        GameRegistry registry = gameManager.getRegistry();
        registry.setState(GameState.SCATTERING);
        World world = Bukkit.getWorld(registry.getUhcWorld());

        if (world == null) return;

        worldManager.clearEntities(world);

        if (!gameManager.getGame().getSetting().isChatBeforePvp()) {
            registry.setChatMuted(true);
        }

        int borderSize = gameManager.getGame().getSetting().getBorderSize();
        int height = borderManager.getBorderHeight();
        borderManager.shrinkBorder(world.getName(), borderSize, height);

        MCSchedulers.getGlobalScheduler().schedule(() -> {
            ReflectionUtils.get().setGameRule(world, "doDaylightCycle", false);
            worldManager.unloadUnusedWorld();

            processPlayersForScattering(world, borderSize);
            new ScatterTask().run(false, 15L);
        }, 20L);
    }

    private void processPlayersForScattering(World world, int borderSize) {
        for (UUID uuid : gameManager.getRegistry().getAlivePlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                handlePlayerScattering(player, world, borderSize);
            }
        }
    }

    private void handlePlayerScattering(Player player, World world, int borderSize) {
        if (gameManager.getRegistry().isClearChat()) {
            player.sendMessage(this.clearChatString);
        }

        Players.clear(player);

        Team team = UHCUtils.getTeam(player);

        if (team == null) {
            team = assignTeam(player);
        }

        setScatterLocationIfNeeded(team, world, borderSize);

        gameManager.getRegistry().getPlayersToScatter().add(player.getUniqueId());
    }

    private Team assignTeam(Player player) {
        if (scenarioManager.isEnabled("Red vs Blue")) {
            return handleRedVsBlueTeam(player);
        } else {
            return handleNormalTeam(player);
        }
    }

    private Team handleRedVsBlueTeam(Player player) {
        Team redTeam = teamManager.getTeams().get(0);
        Team blueTeam = teamManager.getTeams().get(1);

        if (redTeam == null) {
            teamManager.createTeam(0, player);
            return teamManager.getTeams().get(0);
        }

        if (blueTeam == null) {
            teamManager.createTeam(1, player);
            return teamManager.getTeams().get(1);
        }

        Team smallerTeam = redTeam.getMembers(true).size() > blueTeam.getMembers(true).size()
                ? blueTeam : redTeam;

        smallerTeam.addMember(player);
        return smallerTeam;
    }

    private Team handleNormalTeam(Player player) {
        if (gameManager.getGame().getSetting().getTeamSize() == 1 || scenarioManager.isEnabled("Love At First Sight")) {
            teamManager.createTeam(player);
            return UHCUtils.getTeam(player);
        }

        for (Team allTeam : teamManager.getTeams().values()) {
            boolean isFill = allTeam.getStorage().getOrThrow(TeamEx.TEAM_FILL);
            if (isFill && allTeam.getMembers(true).size() < gameManager.getGame().getSetting().getTeamSize()) {
                allTeam.addMember(player);
                return allTeam;
            }
        }

        teamManager.createTeam(player);
        return UHCUtils.getTeam(player);
    }

    private void setScatterLocationIfNeeded(Team team, World world, int borderSize) {
        if (team == null) return;

        if (!team.getStorage().contains(TeamEx.SCATTER_LOCATION)) {
            Location safeLoc = gameManager.findSafeScatterLocation(world, borderSize);
            team.getStorage().put(TeamEx.SCATTER_LOCATION, safeLoc);
        }
    }
}
