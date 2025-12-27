package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.events.player.PlayerDamageEvent;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.scenario.annotations.IncompatibleWith;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

@IncompatibleWith(BoneBreakerScenario.class)
public class NoFallScenario extends Scenario {

    @Override
    public String getName() {
        return "No Fall";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.FEATHER)
                .name("&b" + getName())
                .lore(
                        "&7Fall damage is disabled."
                )
                .build();
    }

    @EventHandler
    public void handlePlayerFall(PlayerDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }
    }
}
