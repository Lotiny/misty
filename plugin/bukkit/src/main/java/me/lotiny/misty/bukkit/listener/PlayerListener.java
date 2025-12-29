package me.lotiny.misty.bukkit.listener;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import io.fairyproject.bukkit.events.player.PlayerDamageEvent;
import io.fairyproject.bukkit.listener.RegisterAsListener;
import io.fairyproject.bukkit.util.LegacyAdventureUtil;
import io.fairyproject.bukkit.util.Players;
import io.fairyproject.container.Autowired;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.mc.MCPlayer;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.impl.MainConfig;
import me.lotiny.misty.bukkit.manager.PracticeManager;
import me.lotiny.misty.bukkit.provider.hotbar.HotBar;
import me.lotiny.misty.bukkit.storage.StorageRegistry;
import me.lotiny.misty.bukkit.task.StartTask;
import me.lotiny.misty.bukkit.utils.LocationEx;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import me.lotiny.misty.bukkit.utils.VersionUtils;
import me.lotiny.misty.shared.event.PlayerPickupItemEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@InjectableComponent
@RequiredArgsConstructor
@RegisterAsListener
public class PlayerListener implements Listener {

    @Autowired
    private static PlayerListener instance;

    private final GameManager gameManager;
    private final PracticeManager practiceManager;
    private final ScenarioManager scenarioManager;
    private final StorageRegistry storageRegistry;

    public static PlayerListener get() {
        return instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        storageRegistry.getProfileStorage().getAsync(uuid.toString())
                .thenAccept(profile -> profile.setName(player.getName()));

        //noinspection deprecation
        event.setJoinMessage(null);

        Players.clear(player);
        LocationEx.LOBBY.teleport(player);

        GameRegistry registry = gameManager.getRegistry();
        registry.getPlayers().putIfAbsent(uuid, true);

        handleJoinMessage(player);
        HotBar.get().apply(player);

        MainConfig.AutoStart autoStart = Config.getMainConfig().getAutoStart();
        if (autoStart.isEnabled() && registry.getStartTask() == null &&
                registry.getPlayers().size() >= autoStart.getMinPlayers()) {

            int time = autoStart.getTimer();
            if (time <= 60 && practiceManager.isOpened()) {
                practiceManager.setOpened(false, Bukkit.getConsoleSender());
            }

            StartTask task = new StartTask(time, false);
            task.run(true, 20L);
            registry.setStartTask(task);
            PlayerUtils.playSound(XSound.UI_BUTTON_CLICK);
        }
    }

    private void handleJoinMessage(Player player) {
        MCPlayer mcPlayer = MCPlayer.from(player);
        if (mcPlayer != null) {
            List<String> scenarios = new ArrayList<>();
            scenarioManager.getEnabledScenarios(gameManager).forEach(scenario -> scenarios.add(scenario.getName()));
            TagResolver tagResolver = TagResolver.builder()
                    .resolvers(
                            Placeholder.parsed("player", player.getName()),
                            Placeholder.parsed("host", gameManager.getRegistry().getHostName()),
                            Placeholder.parsed("version", VersionUtils.getPluginVersion()),
                            Placeholder.parsed("border", String.valueOf(gameManager.getGame().getSetting().getBorderSize())),
                            Placeholder.parsed("type", UHCUtils.getGameType()),
                            Placeholder.parsed("scenario", Arrays.toString(scenarios.toArray()).replace("[", "").replace("]", ""))
                    )
                    .build();

            Config.getMainConfig().getJoinMessage().forEach(message -> {
                mcPlayer.sendMessage(LegacyAdventureUtil.decode(message, tagResolver));
            });
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Profile profile = storageRegistry.getProfile(uuid);

        //noinspection deprecation
        event.setQuitMessage(null);

        GameRegistry registry = gameManager.getRegistry();
        practiceManager.getPlayers().remove(uuid);
        registry.getPlayersToScatter().remove(uuid);
        registry.getPlayers().remove(uuid);

        storageRegistry.getProfileStorage().saveAsync(profile);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBLockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamage(PlayerDamageEvent event) {
        Player player = event.getPlayer();
        if (!practiceManager.isInPractice(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        event.getInventory().setResult(XMaterial.AIR.parseItem());
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }
}
