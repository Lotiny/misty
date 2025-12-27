package me.lotiny.misty.bukkit.game.listeners;

import io.fairyproject.bukkit.events.player.PlayerDamageByPlayerEvent;
import io.fairyproject.container.Autowired;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.api.profile.stats.StatType;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.storage.StorageRegistry;
import me.lotiny.misty.bukkit.utils.KeyEx;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import me.lotiny.misty.bukkit.utils.cooldown.CombatCooldown;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class CombatListener implements Listener {

    @Autowired
    private static GameManager gameManager;
    @Autowired
    private static ScenarioManager scenarioManager;
    @Autowired
    private static StorageRegistry storageRegistry;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerDamageByPlayer(PlayerDamageByPlayerEvent event) {
        if (event.isCancelled()) return;

        Player damaged = event.getPlayer();
        Team damagedTeam = UHCUtils.getTeam(damaged);

        Player damager = event.getDamager();
        Team damagerTeam = UHCUtils.getTeam(damager);

        if (damagedTeam != damagerTeam) {
            Profile profile = storageRegistry.getProfile(damager.getUniqueId());
            profile.getStats(StatType.TOTAL_DAMAGE).increase((int) Math.round(event.getFinalDamage()));

            addCombatCooldown(damagedTeam, damagerTeam);
            addCombatCooldown(damagerTeam, damagedTeam);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Team team = UHCUtils.getTeam(player);
        CombatCooldown cooldown = team.getStorage().getOrNull(KeyEx.COMBAT_COOLDOWN_KEY);
        if (cooldown == null) return;

        removeCombatCooldown(team);
    }

    private void addCombatCooldown(Team team, Team targetTeam) {
        CombatCooldown cooldown = team.getStorage().getOrNull(KeyEx.COMBAT_COOLDOWN_KEY);
        if (cooldown == null) {
            createCooldown(team, targetTeam);
            return;
        }

        if (cooldown.getTeam().isSame(targetTeam)) {
            cooldown.addCooldown(team, 30_000L);
        } else {
            cooldown.removeCooldown(team);
            createCooldown(team, targetTeam);
        }
    }

    private void createCooldown(Team team, Team targetTeam) {
        CombatCooldown cooldown = new CombatCooldown(30_000L, targetTeam);
        cooldown.addCooldown(team);
        cooldown.removalListener(t -> handleCooldownRemoval(team, targetTeam));

        team.getStorage().put(KeyEx.COMBAT_COOLDOWN_KEY, cooldown);
        UHCUtils.sendAlert("&cTeam #" + team.getId() + " has entered combat with Team #" + targetTeam.getId() + ".");
    }

    private void handleCooldownRemoval(Team team, Team targetTeam) {
        team.getStorage().remove(KeyEx.COMBAT_COOLDOWN_KEY);
        UHCUtils.sendAlert("&cTeam #" + team.getId() + " has left combat with Team #" + targetTeam.getId() + ".");

        if (scenarioManager.isEnabled("Do Not Disturb")) {
            Player target = Bukkit.getPlayer(targetTeam.getMembers(false).getFirst());
            team.sendMessage(Message.DO_NOT_DISTURB_UNLINKED_WITH.toString()
                    .replace("<linked>", (gameManager.getGame().getSetting().getTeamSize() > 1) ? "Team #" + targetTeam.getId() : target == null ? "null" : target.getName()));
        }
    }

    private void removeCombatCooldown(Team team) {
        CombatCooldown cooldown = team.getStorage().getOrThrow(KeyEx.COMBAT_COOLDOWN_KEY);
        Team opponentTeam = cooldown.getTeam();

        cooldown.removeCooldown(team);
        opponentTeam.getStorage().getOrThrow(KeyEx.COMBAT_COOLDOWN_KEY).removeCooldown(opponentTeam);

        team.getStorage().remove(KeyEx.COMBAT_COOLDOWN_KEY);
        opponentTeam.getStorage().remove(KeyEx.COMBAT_COOLDOWN_KEY);

        if (scenarioManager.isEnabled("Do Not Disturb")) {
            sendUnlinkedMessage(team, opponentTeam);
            sendUnlinkedMessage(opponentTeam, team);
        }
    }

    private void sendUnlinkedMessage(Team team, Team opponentTeam) {
        Player target = Bukkit.getPlayer(opponentTeam.getMembers(false).getFirst());
        team.sendMessage(Message.DO_NOT_DISTURB_UNLINKED_WITH.toString()
                .replace("<linked>", (gameManager.getGame().getSetting().getTeamSize() > 1) ? "Team #" + opponentTeam.getId() : target == null ? "null" : target.getName()));
    }
}
