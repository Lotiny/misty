package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.events.player.PlayerDamageByPlayerEvent;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class DoNotDisturbScenario extends Scenario {

    @Autowired
    private static GameManager gameManager;

    @Override
    public String getName() {
        return "Do Not Disturb";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.RED_BED)
                .name("&b" + getName())
                .lore(
                        "&7If you hit a player, it will lock you with that",
                        "&7player or team for 30 seconds. Every time you hit",
                        "&7that player or team, the timer will refresh until",
                        "&7the 30 seconds are up and can be attacked by another player or team."
                )
                .build();
    }

    @EventHandler
    public void handlePlayerDamageByPlayer(PlayerDamageByPlayerEvent event) {
        if (event.isCancelled()) return;

        Player damager = event.getDamager();
        Player damaged = event.getPlayer();

        Team damagerTeam = UHCUtils.getTeam(damager);
        Team damagedTeam = UHCUtils.getTeam(damaged);

        if (!UHCUtils.isInCombat(damagerTeam) && !UHCUtils.isInCombat(damagedTeam)) {
            sendLinkedMessage(damagerTeam, damagedTeam, damager.getName(), damaged.getName());
            return;
        }

        if (!UHCUtils.isCombatWith(damagerTeam, damagedTeam) || !UHCUtils.isCombatWith(damagedTeam, damagerTeam)) {
            event.setCancelled(true);
            damagerTeam.sendMessage(Message.DO_NOT_DISTURB_NOT_LINKED_TO.toString());
        }
    }

    private void sendLinkedMessage(Team damagerTeam, Team damagedTeam, String damagerName, String damagedName) {
        String damagerMessage = (gameManager.getGame().getSetting().getTeamSize() > 1) ? "Team #" + damagedTeam.getId() : damagedName;
        String damagedMessage = (gameManager.getGame().getSetting().getTeamSize() > 1) ? "Team #" + damagerTeam.getId() : damagerName;
        damagerTeam.sendMessage(Message.DO_NOT_DISTURB_LINKED_WITH.toString()
                .replace("<linked>", damagerMessage));
        damagedTeam.sendMessage(Message.DO_NOT_DISTURB_LINKED_WITH.toString()
                .replace("<linked>", damagedMessage));
    }
}
