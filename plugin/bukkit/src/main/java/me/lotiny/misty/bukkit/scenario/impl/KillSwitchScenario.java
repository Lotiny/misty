package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class KillSwitchScenario extends Scenario {

    @Override
    public String getName() {
        return "Kill Switch";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.STONE_SWORD)
                .name("&b" + getName())
                .lore(
                        "&7Everytime you get a kill your inventory",
                        "&7will switched with victim's inventory"
                )
                .build();
    }

    @EventHandler
    public void handlePlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();

        if (victim.getKiller() != null) {
            Player killer = victim.getKiller();

            event.setKeepInventory(true);
            event.getDrops().clear();
            killer.getInventory().setArmorContents(victim.getInventory().getArmorContents());
            killer.getInventory().setContents(victim.getInventory().getContents());
        }
    }
}
