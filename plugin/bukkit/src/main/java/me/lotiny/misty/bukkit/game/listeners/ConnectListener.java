package me.lotiny.misty.bukkit.game.listeners;

import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.bukkit.util.Players;
import io.fairyproject.container.Autowired;
import io.fairyproject.mc.scheduler.MCSchedulers;
import io.fairyproject.metadata.MetadataMap;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.registry.CombatLogger;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.api.team.TeamManager;
import me.lotiny.misty.bukkit.game.registry.CombatLoggerImpl;
import me.lotiny.misty.bukkit.provider.hotbar.HotBar;
import me.lotiny.misty.bukkit.storage.StorageRegistry;
import me.lotiny.misty.bukkit.utils.KeyEx;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.Snapshot;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;
import java.util.UUID;

public class ConnectListener implements Listener {

    @Autowired
    private static GameManager gameManager;
    @Autowired
    private static TeamManager teamManager;
    @Autowired
    private static StorageRegistry storageRegistry;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        storageRegistry.getProfileStorage().getAsync(uuid.toString())
                .thenAccept(profile -> profile.setName(player.getName()));

        //noinspection deprecation
        event.setJoinMessage(null);

        if (UHCUtils.getTeam(player) == null) {
            Team team = teamManager.createTeam(player);
            teamManager.getTeams().remove(team.getId());
        }

        Optional<CombatLogger> loggerOpt = gameManager.findCombatLogger(player);

        if (loggerOpt.isEmpty()) {
            Players.clear(player);

            MCSchedulers.getGlobalScheduler().schedule(() -> {
                player.setAllowFlight(true);
                player.setFlying(true);
                player.setGameMode(GameMode.ADVENTURE);
                player.teleport(UHCUtils.getCenter());
                HotBar.get().apply(player);
            }, 5L);

        } else {
            CombatLogger logger = loggerOpt.get();
            Villager loggerEntity = logger.getSpawnedEntity();

            if (loggerEntity != null && loggerEntity.isValid() && !loggerEntity.isDead()) {
                MetadataMap metadata = Metadata.provideForPlayer(player);
                Snapshot snapshot = metadata.getOrThrow(KeyEx.SNAPSHOT_KEY);

                player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));

                Team team = snapshot.getTeam();
                UHCUtils.setTeam(player, team);
                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(loggerEntity.getLocation());
                int maxHealth = Math.max((int) snapshot.getMaxHealth(), 20);
                PlayerUtils.setMaxHealth(player, maxHealth);
                player.setHealth(snapshot.getHealth() > 0 ? snapshot.getHealth() : maxHealth);
                player.setLevel(snapshot.getLevel());
                player.setExp(snapshot.getExp());
                player.getInventory().setContents(snapshot.getItems());
                player.getInventory().setArmorContents(snapshot.getArmors());
                snapshot.getEffects().forEach(player::addPotionEffect);

                metadata.remove(KeyEx.SNAPSHOT_KEY);

                gameManager.getRegistry().getCombatLoggers().remove(loggerEntity.getUniqueId());
                gameManager.getCombatLoggerCooldown().removeCooldown(logger);
                loggerEntity.remove();
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Profile profile = storageRegistry.getProfile(uuid);

        //noinspection deprecation
        event.setQuitMessage(null);

        if (UHCUtils.isAlive(uuid)) {
            CombatLoggerImpl logger = new CombatLoggerImpl(player);
            logger.setNameFormat("&c[CombatLogger] " + player.getName());
            logger.spawn(player.getLocation());

            gameManager.registerPlayerDeath(player);
            gameManager.checkWinner();
        }

        storageRegistry.getProfileStorage().saveAsync(profile);
    }
}
