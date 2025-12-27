package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.util.CC;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.scenario.annotations.IncompatibleWith;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

@IncompatibleWith(BowlessScenario.class)
public class SwitcherooScenario extends Scenario {

    @Override
    public String getName() {
        return "Switcheroo";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.BOW)
                .name("&b" + getName())
                .lore(
                        "&7Everytime you shoot an arrow and hit the player",
                        "&7you and that player will switch the location."
                )
                .build();
    }

    @EventHandler
    public void handleBowShot(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player damaged && event.getDamager() instanceof Arrow shooter) {

            if (!(shooter.getShooter() instanceof Player damager)) return;

            Location damagedLocation = damaged.getLocation().clone();
            Location damagerLocation = damager.getLocation().clone();

            damaged.teleport(damagerLocation);
            damager.teleport(damagedLocation);

            damaged.sendMessage(CC.GOLD + "Switch!");
            damager.sendMessage(CC.GOLD + "Switch!");
        }
    }
}
