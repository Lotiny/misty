package me.lotiny.misty.bukkit.game.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import io.fairyproject.bukkit.events.player.EntityDamageByPlayerEvent;
import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.container.Autowired;
import io.fairyproject.metadata.MetadataMap;
import io.fairyproject.util.ConditionUtils;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.game.registry.CombatLogger;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.api.profile.stats.StatType;
import me.lotiny.misty.api.profile.stats.Stats;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.bukkit.game.registry.CombatLoggerImpl;
import me.lotiny.misty.bukkit.storage.StorageRegistry;
import me.lotiny.misty.bukkit.utils.KeyEx;
import me.lotiny.misty.bukkit.utils.Snapshot;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import me.lotiny.misty.bukkit.utils.Utilities;
import me.lotiny.misty.bukkit.utils.elo.EloUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.UUID;

public class CombatLoggerListener implements Listener {

    @Autowired
    private static GameManager gameManager;
    @Autowired
    private static ScenarioManager scenarioManager;
    @Autowired
    private static StorageRegistry storageRegistry;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        if (Metadata.provideForEntity(event.getEntity()).has(CombatLoggerImpl.COMBAT_LOGGER_KEY)) {
            handleCombatLoggerDeath(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (Metadata.provideForEntity(event.getRightClicked()).has(CombatLoggerImpl.COMBAT_LOGGER_KEY)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityPortal(EntityPortalEvent event) {
        if (Metadata.provideForEntity(event.getEntity()).has(CombatLoggerImpl.COMBAT_LOGGER_KEY)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByPlayerEvent event) {
        if (Metadata.provideForEntity(event.getEntity()).has(CombatLoggerImpl.COMBAT_LOGGER_KEY)) {
            GameRegistry registry = gameManager.getRegistry();
            CombatLogger logger = registry.getCombatLoggers().get(event.getEntity().getUniqueId());

            if (logger != null) {
                Snapshot loggerData = Metadata.provideForPlayer(logger.getPlayerUniqueId()).getOrThrow(KeyEx.SNAPSHOT_KEY);

                if (registry.getState() != GameState.INGAME) {
                    event.setCancelled(true);
                    return;
                }

                if (!registry.isPvpEnabled()) {
                    event.setCancelled(true);
                    return;
                }

                Player damager = event.getDamager();
                Team damagerTeam = UHCUtils.getTeam(damager);
                Team loggerTeam = loggerData.getTeam();
                if (damagerTeam.isSame(loggerTeam)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityPressurePlate(EntityInteractEvent event) {
        if (XTag.PRESSURE_PLATES.isTagged(XMaterial.matchXMaterial(event.getBlock().getType())) && Metadata.provideForEntity(event.getEntity()).has(CombatLoggerImpl.COMBAT_LOGGER_KEY)) {
            event.setCancelled(true);
        }
    }

    private void handleCombatLoggerDeath(EntityDeathEvent event) {
        GameRegistry registry = gameManager.getRegistry();
        UUID entityId = event.getEntity().getUniqueId();

        CombatLogger logger = registry.getCombatLoggers().remove(entityId);
        ConditionUtils.notNull(logger, "CombatLogger not found");

        UUID playerUuid = logger.getPlayerUniqueId();
        MetadataMap playerMeta = Metadata.provideForPlayer(playerUuid);

        clearLoggerInventory(playerUuid);

        Player killer = event.getEntity().getKiller();
        ConditionUtils.notNull(killer, "Killer is null");

        Profile killerProfile = storageRegistry.getProfile(killer.getUniqueId());
        Profile dyingProfile = storageRegistry.getProfile(playerUuid);

        updateKillsAndTeams(killer);
        Snapshot snapshot = updateSnapshotLocation(logger, playerMeta);

        updateElo(killerProfile, dyingProfile);
        broadcastCombatLoggerDeath(killerProfile, dyingProfile, playerMeta, killer);

        logger.dropInventory();
        scenarioManager.dropScenarioItems(logger.getSpawnedEntity().getLocation());

        finalizeDeath(registry, snapshot, entityId, dyingProfile);
    }

    private void clearLoggerInventory(UUID playerUuid) {
        Player target = Bukkit.getPlayer(playerUuid);
        if (target != null) {
            target.getInventory().clear();
            target.getInventory().setArmorContents(null);
            target.saveData();
        }
    }

    private void updateKillsAndTeams(Player killer) {
        UHCUtils.increaseGameKills(killer);
        if (gameManager.getGame().getSetting().getTeamSize() > 1) {
            Team killerTeam = UHCUtils.getTeam(killer);
            if (killerTeam != null) {
                killerTeam.setTeamKills(killerTeam.getTeamKills() + 1);
            }
        }
    }

    private Snapshot updateSnapshotLocation(CombatLogger logger, MetadataMap playerMeta) {
        Snapshot snapshot = playerMeta.getOrThrow(KeyEx.SNAPSHOT_KEY);
        snapshot.setLocation(logger.getSpawnedEntity().getLocation());
        return snapshot;
    }

    private void updateElo(Profile killerProfile, Profile dyingProfile) {
        int killerElo = killerProfile.getStats(StatType.ELO).getAmount();
        int dyingElo = dyingProfile.getStats(StatType.ELO).getAmount();

        // Killer updates
        int newKillerElo = EloUtils.getNewRating(killerElo, dyingElo, true);
        killerProfile.getStats(StatType.KILLS).increase();
        killerProfile.getStats(StatType.ELO).setAmount(newKillerElo);

        // Dying player updates
        int newDyingElo = EloUtils.getNewRating(dyingElo / 2, killerElo, false);
        dyingProfile.getStats(StatType.DEATHS).increase();
        dyingProfile.getStats(StatType.ELO).setAmount(newDyingElo);
    }

    private void broadcastCombatLoggerDeath(Profile killerProfile, Profile dyingProfile, MetadataMap playerMeta, Player killer) {
        int dyingKills = playerMeta.getOrDefault(KeyEx.GAME_KILLS_KEY, new Stats()).getAmount();
        int killerKills = UHCUtils.getGameKills(killer);

        Utilities.broadcast(String.format(
                "&7(Combat-Logger) &c%s &7[&c%d&7]&e was slain by &a%s &7[&a%d&7]",
                dyingProfile.getName(),
                dyingKills,
                killerProfile.getName(),
                killerKills
        ));
    }

    private void finalizeDeath(GameRegistry registry, Snapshot snapshot, UUID entityId, Profile dyingProfile) {
        registry.getPlayers().replace(snapshot.getUuid(), false);
        registry.getCombatLoggers().remove(entityId);
        gameManager.checkWinner();
        storageRegistry.getProfileStorage().saveAsync(dyingProfile);
    }
}
