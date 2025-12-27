package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.api.team.TeamManager;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.scenario.annotations.IncompatibleWith;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

@IncompatibleWith(RedVsBlueScenario.class)
public class LoveAtFirstSightScenario extends Scenario {

    @Autowired
    private static TeamManager teamManager;

    @Override
    public String getName() {
        return "Love At First Sight";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.ROSE_BUSH)
                .name("&b" + getName())
                .lore(
                        "&7The first person you right-click to will be your teammate!"
                )
                .build();
    }

    @EventHandler
    public void handlePlayerTeamUp(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player target) {
            Player player = event.getPlayer();

            Team playerTeam = UHCUtils.getTeam(player);
            Team targetTeam = UHCUtils.getTeam(target);

            if (!UHCUtils.isAlive(player.getUniqueId()) || !UHCUtils.isAlive(target.getUniqueId()) || playerTeam.getMembers(false).size() == 2)
                return;

            if (targetTeam.getMembers(false).size() == 2) {
                player.sendMessage(Message.LOVE_AT_FIRST_SIGHT_ALREADY_HAVE_TEAM.toString());
                return;
            }

            teamManager.getTeams().remove(targetTeam.getId());
            playerTeam.addMember(target);
            player.sendMessage(Message.LOVE_AT_FIRST_SIGHT_TEAM_WITH.toString()
                    .replace("<player>", target.getName()));
            target.sendMessage(Message.LOVE_AT_FIRST_SIGHT_TEAM_WITH.toString()
                    .replace("<player>", player.getName()));
        }
    }
}
