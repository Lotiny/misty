package me.lotiny.misty.bukkit.game.listeners;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.bukkit.util.Players;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import io.fairyproject.metadata.MetadataMap;
import io.fairyproject.util.CC;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.api.profile.stats.StatType;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.api.team.TeamManager;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.provider.hotbar.HotBar;
import me.lotiny.misty.bukkit.storage.StorageRegistry;
import me.lotiny.misty.bukkit.utils.KeyEx;
import me.lotiny.misty.bukkit.utils.Snapshot;
import me.lotiny.misty.bukkit.utils.TeamEx;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import me.lotiny.misty.bukkit.utils.elo.EloUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerDeathListener implements Listener {

    @Autowired
    private static GameManager gameManager;
    @Autowired
    private static ScenarioManager scenarioManager;
    @Autowired
    private static TeamManager teamManager;
    @Autowired
    private static StorageRegistry storageRegistry;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerDeathEvent(PlayerDeathEvent event) {
        GameRegistry registry = gameManager.getRegistry();

        Player player = event.getEntity();
        if (!UHCUtils.isAlive(player.getUniqueId())) return;

        Profile profile = storageRegistry.getProfile(player.getUniqueId());
        Player killer = player.getKiller();
        Profile killerProfile = killer == null ? null : storageRegistry.getProfile(killer.getUniqueId());
        Team team = UHCUtils.getTeam(player);

        gameManager.registerPlayerDeath(player);
        profile.getStats(StatType.DEATHS).increase();

        if (Config.getMainConfig().isStrikeLightningOnDeath()) {
            player.getWorld().strikeLightningEffect(player.getLocation());
        }

        handleDeathMessageAndElo(event, player, profile, killer, killerProfile);

        if (!scenarioManager.isEnabled("Safeloot") && !scenarioManager.isEnabled("Timebomb")) {
            dropPlayerHead(player);
            scenarioManager.dropScenarioItems(player.getLocation());
        }

        cleanupPlayerAndTeam(registry, player, team);

        gameManager.checkWinner();
    }

    @EventHandler
    public void handlePlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        GameRegistry registry = gameManager.getRegistry();

        if (!UHCUtils.isAlive(uuid)) return;

        World uhcWorld = Bukkit.getWorld(registry.getUhcWorld());
        if (uhcWorld != null) {
            Location respawnLocation = new Location(uhcWorld, 0, uhcWorld.getHighestBlockYAt(0, 0) + 10, 0);

            event.setRespawnLocation(respawnLocation);

            Players.clear(player);
            player.setGameMode(GameMode.ADVENTURE);
            registry.getPlayers().replace(uuid, false);

            player.setAllowFlight(true);
            player.setFlying(true);
            HotBar.get().apply(player);
        }
    }

    @SuppressWarnings("deprecation")
    private void handleDeathMessageAndElo(PlayerDeathEvent event, Player player, Profile profile, Player killer, Profile killerProfile) {
        String deathMessage = event.getDeathMessage();
        if (deathMessage == null) {
            deathMessage = player.getName() + " died";
        }

        MetadataMap metadata = Metadata.provideForPlayer(player);

        if (killerProfile != null) {
            UHCUtils.increaseGameKills(killer);
            killerProfile.getStats(StatType.KILLS).increase();

            event.setDeathMessage(CC.translate(deathMessage
                    .replace(player.getName(), "&c" + player.getName() + "&7[&c" + UHCUtils.getGameKills(player) + "&7]&e")
                    .replace(killer.getName(), "&a" + killer.getName() + "&7[&a" + UHCUtils.getGameKills(killer) + "&7]&e") + "&r"));

            if (gameManager.getGame().getSetting().getTeamSize() > 1) {
                Team killerTeam = UHCUtils.getTeam(killer);
                if (killerTeam != null) {
                    killerTeam.setTeamKills(killerTeam.getTeamKills() + 1);
                }
            }

            updateElo(profile, killerProfile, metadata);
        } else {
            event.setDeathMessage(CC.translate(deathMessage
                    .replace(player.getName(), "&c" + player.getName() + "&7[&c" + UHCUtils.getGameKills(player) + "&7]&e")));

            updateEloNoKiller(profile, metadata);
        }
    }

    private void updateElo(Profile dyingProfile, Profile killerProfile, MetadataMap metadata) {
        int oldPlayerElo = dyingProfile.getStats(StatType.ELO).getAmount();
        int oldKillerElo = killerProfile.getStats(StatType.ELO).getAmount();

        int newPlayerElo = EloUtils.getNewRating(oldPlayerElo, oldKillerElo, false);
        int newKillerElo = EloUtils.getNewRating(oldKillerElo, oldPlayerElo, true);

        Snapshot snapshot = metadata.getOrThrow(KeyEx.SNAPSHOT_KEY);
        snapshot.setElo(oldPlayerElo - newPlayerElo);
        metadata.put(KeyEx.SNAPSHOT_KEY, snapshot);

        dyingProfile.getStats(StatType.ELO).setAmount(newPlayerElo);
        killerProfile.getStats(StatType.ELO).setAmount(newKillerElo);
    }

    private void updateEloNoKiller(Profile dyingProfile, MetadataMap metadata) {
        int oldPlayerElo = dyingProfile.getStats(StatType.ELO).getAmount();
        int newPlayerElo = EloUtils.getNewRating(oldPlayerElo, 1000, false);

        Snapshot snapshot = metadata.getOrThrow(KeyEx.SNAPSHOT_KEY);
        snapshot.setElo(oldPlayerElo - newPlayerElo);
        metadata.put(KeyEx.SNAPSHOT_KEY, snapshot);

        dyingProfile.getStats(StatType.ELO).setAmount(newPlayerElo);
    }

    private void cleanupPlayerAndTeam(GameRegistry registry, Player player, Team team) {
        registry.getPlayers().replace(player.getUniqueId(), false);

        if (team.getMembers(true).isEmpty()) {
            if (scenarioManager.isEnabled("Backpacks")) {
                for (ItemStack item : team.getStorage().getOrThrow(TeamEx.TEAM_INVENTORY).getContents()) {
                    if (item != null && item.getType() != XMaterial.AIR.get()) {
                        UHCUtils.dropItem(player.getLocation(), item);
                    }
                }
            }
            teamManager.getTeams().remove(team.getId());
        }
    }

    private void dropPlayerHead(Player player) {
        ItemStack skull = ItemBuilder.of(XMaterial.PLAYER_HEAD)
                .skull(player.getName())
                .build();

        UHCUtils.dropItem(player.getLocation(), skull);
    }
}
