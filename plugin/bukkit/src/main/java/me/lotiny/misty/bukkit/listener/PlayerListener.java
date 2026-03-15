package me.lotiny.misty.bukkit.listener;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import io.fairyproject.bukkit.events.BukkitEventFilter;
import io.fairyproject.bukkit.events.BukkitEventNode;
import io.fairyproject.bukkit.events.player.PlayerDamageByPlayerEvent;
import io.fairyproject.bukkit.events.player.PlayerDamageEvent;
import io.fairyproject.bukkit.util.LegacyAdventureUtil;
import io.fairyproject.bukkit.util.Players;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.container.PreDestroy;
import io.fairyproject.event.EventListener;
import io.fairyproject.event.EventNode;
import io.fairyproject.mc.MCPlayer;
import io.fairyproject.mc.scheduler.MCSchedulers;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.impl.MainConfig;
import me.lotiny.misty.bukkit.manager.PracticeManager;
import me.lotiny.misty.bukkit.provider.hotbar.HotBar;
import me.lotiny.misty.bukkit.storage.StorageRegistry;
import me.lotiny.misty.bukkit.task.StartTask;
import me.lotiny.misty.bukkit.utils.GoldenHead;
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
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@InjectableComponent
@RequiredArgsConstructor
public class PlayerListener {

    private final GameManager gameManager;
    private final ScenarioManager scenarioManager;
    private final PracticeManager practiceManager;
    private final StorageRegistry storageRegistry;
    private final BukkitEventNode globalNode;

    private List<String> joinMessage;

    private boolean autoStart;
    private int autoStartTime;
    private int minPlayers;

    private EventNode<Event> eventNode;

    @PostInitialize
    public void onPostInit() {
        MainConfig config = Config.getMainConfig();
        this.joinMessage = config.getJoinMessage();

        this.autoStart = config.getAutoStart().isEnabled();
        this.autoStartTime = config.getAutoStart().getTimer();
        this.minPlayers = config.getAutoStart().getMinPlayers();

        this.eventNode = EventNode.type("player-listeners", BukkitEventFilter.ALL);

        EventListener<PlayerJoinEvent> playerJoinListener = EventListener.builder(PlayerJoinEvent.class)
                .expireWhen(event -> gameManager.getRegistry().getState() == GameState.INGAME)
                .handler(event -> {
                    Player player = event.getPlayer();
                    UUID uuid = player.getUniqueId();

                    storageRegistry
                            .getProfileStorage()
                            .getAsync(uuid.toString())
                            .thenAccept(profile -> profile.setName(player.getName()));

                    event.setJoinMessage(null);

                    Players.clear(player);
                    LocationEx.LOBBY.teleport(player);

                    GameRegistry registry = gameManager.getRegistry();
                    registry.getPlayers().putIfAbsent(uuid, true);

                    handleJoinMessage(player);
                    HotBar.get().apply(player);

                    if (autoStart
                            && registry.getStartTask() == null
                            && registry.getPlayers().size() >= minPlayers) {
                        if (autoStartTime <= 60 && practiceManager.isOpened()) {
                            practiceManager.setOpened(false, Bukkit.getConsoleSender());
                        }

                        StartTask task = new StartTask(autoStartTime, false);
                        task.run(true, 20L);
                        registry.setStartTask(task);
                        PlayerUtils.playSound(XSound.UI_BUTTON_CLICK);
                    }
                })
                .build();

        eventNode.addListener(playerJoinListener);

        EventListener<PlayerQuitEvent> playerQuitEvent = EventListener.builder(PlayerQuitEvent.class)
                .expireWhen(event -> gameManager.getRegistry().getState() == GameState.INGAME)
                .handler(event -> {
                    Player player = event.getPlayer();
                    UUID uuid = player.getUniqueId();
                    Profile profile = storageRegistry.getProfile(uuid);

                    event.setQuitMessage(null);

                    GameRegistry registry = gameManager.getRegistry();
                    practiceManager.getPlayers().remove(uuid);
                    registry.getPlayersToScatter().remove(uuid);
                    registry.getPlayers().remove(uuid);

                    storageRegistry.getProfileStorage().saveAsync(profile);
                })
                .build();

        eventNode.addListener(playerQuitEvent);

        EventListener<BlockBreakEvent> blockBreakEvent = EventListener.builder(BlockBreakEvent.class)
                .expireWhen(event -> gameManager.getRegistry().getState() == GameState.INGAME)
                .handler(event -> {
                    Player player = event.getPlayer();
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        event.setCancelled(true);
                    }
                })
                .build();

        eventNode.addListener(blockBreakEvent);

        EventListener<BlockPlaceEvent> blockPlaceEvent = EventListener.builder(BlockPlaceEvent.class)
                .expireWhen(event -> gameManager.getRegistry().getState() == GameState.INGAME)
                .handler(event -> {
                    Player player = event.getPlayer();
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        event.setCancelled(true);
                    }
                })
                .build();

        eventNode.addListener(blockPlaceEvent);

        EventListener<PlayerPickupItemEvent> playerPickupItemEvent = EventListener.builder(PlayerPickupItemEvent.class)
                .expireWhen(event -> gameManager.getRegistry().getState() == GameState.INGAME)
                .handler(event -> {
                    Player player = event.getPlayer();
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        event.setCancelled(true);
                    }
                })
                .build();

        eventNode.addListener(playerPickupItemEvent);

        EventListener<PlayerDropItemEvent> playerDropItemEvent = EventListener.builder(PlayerDropItemEvent.class)
                .expireWhen(event -> gameManager.getRegistry().getState() == GameState.INGAME)
                .handler(event -> {
                    Player player = event.getPlayer();
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        event.setCancelled(true);
                    }
                })
                .build();

        eventNode.addListener(playerDropItemEvent);

        EventListener<PlayerDamageEvent> playerDamageEvent = EventListener.builder(PlayerDamageEvent.class)
                .expireWhen(event -> gameManager.getRegistry().getState() == GameState.INGAME)
                .handler(event -> {
                    Player player = event.getPlayer();
                    if (!practiceManager.isInPractice(player)) {
                        event.setCancelled(true);
                    }
                })
                .build();

        eventNode.addListener(playerDamageEvent);

        EventListener<PlayerDamageByPlayerEvent> playerDamageByPlayerEvent = EventListener.builder(
                        PlayerDamageByPlayerEvent.class)
                .expireWhen(event -> gameManager.getRegistry().getState() == GameState.INGAME)
                .handler(event -> {
                    Player player = event.getPlayer();
                    Player damager = event.getDamager();
                    if (!practiceManager.isInPractice(player) || !practiceManager.isInPractice(damager)) {
                        event.setCancelled(true);
                    }
                })
                .build();

        eventNode.addListener(playerDamageByPlayerEvent);

        EventListener<PrepareItemCraftEvent> prepareItemCraftEvent = EventListener.builder(PrepareItemCraftEvent.class)
                .expireWhen(event -> gameManager.getRegistry().getState() == GameState.INGAME)
                .handler(event -> event.getInventory().setResult(XMaterial.AIR.parseItem()))
                .build();

        eventNode.addListener(prepareItemCraftEvent);

        EventListener<FoodLevelChangeEvent> foodLevelChangeEvent = EventListener.builder(FoodLevelChangeEvent.class)
                .expireWhen(event -> gameManager.getRegistry().getState() == GameState.INGAME)
                .handler(event -> event.setCancelled(true))
                .build();

        eventNode.addListener(foodLevelChangeEvent);

        EventListener<PlayerDeathEvent> playerDeathEvent = EventListener.builder(PlayerDeathEvent.class)
                .expireWhen(event -> gameManager.getRegistry().getState() == GameState.INGAME)
                .handler(event -> {
                    Player player = event.getEntity();
                    Player killer = player.getKiller();
                    if (!practiceManager.isInPractice(player)) {
                        return;
                    }

                    event.setDroppedExp(0);
                    event.getDrops().clear();

                    practiceManager.broadcast(event.getDeathMessage());
                    if (killer != null) {
                        killer.getInventory().addItem(GoldenHead.getItem());
                    }

                    event.setDeathMessage(null);

                    MCSchedulers.getGlobalScheduler()
                            .schedule(() -> player.spigot().respawn(), 5L);
                })
                .build();

        eventNode.addListener(playerDeathEvent);

        EventListener<PlayerRespawnEvent> playerRespawnEvent = EventListener.builder(PlayerRespawnEvent.class)
                .expireWhen(event -> gameManager.getRegistry().getState() == GameState.INGAME)
                .handler(event -> {
                    Player player = event.getPlayer();
                    if (!practiceManager.isInPractice(player)) {
                        return;
                    }

                    practiceManager.getPlayers().remove(player.getUniqueId());
                    event.setRespawnLocation(LocationEx.LOBBY.getLocation());

                    MCSchedulers.getGlobalScheduler()
                            .schedule(() -> HotBar.get().apply(player), 5L);
                })
                .build();

        eventNode.addListener(playerRespawnEvent);

        globalNode.addChild(eventNode);
    }

    @PreDestroy
    public void onPreDestroy() {
        globalNode.removeChild(eventNode);
    }

    private void handleJoinMessage(Player player) {
        MCPlayer mcPlayer = MCPlayer.from(player);
        if (mcPlayer != null) {
            List<String> scenarios = new ArrayList<>();
            scenarioManager.getEnabledScenarios().forEach(scenario -> scenarios.add(scenario.getName()));
            TagResolver tagResolver = TagResolver.builder()
                    .resolvers(
                            Placeholder.parsed("player", player.getName()),
                            Placeholder.parsed("host", gameManager.getRegistry().getHostName()),
                            Placeholder.parsed("version", VersionUtils.getPluginVersion()),
                            Placeholder.parsed(
                                    "border",
                                    String.valueOf(
                                            gameManager.getGame().getSetting().getBorderSize())),
                            Placeholder.parsed("type", UHCUtils.getGameType()),
                            Placeholder.parsed(
                                    "scenario",
                                    Arrays.toString(scenarios.toArray())
                                            .replace("[", "")
                                            .replace("]", "")))
                    .build();

            joinMessage.forEach(message -> {
                mcPlayer.sendMessage(LegacyAdventureUtil.decode(message, tagResolver));
            });
        }
    }
}
