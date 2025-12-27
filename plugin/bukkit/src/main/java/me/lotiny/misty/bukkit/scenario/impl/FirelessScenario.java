package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.events.player.PlayerDamageEvent;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.scenario.annotations.IncompatibleWith;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

@IncompatibleWith({PyroScenario.class, ColdWeaponScenario.class})
public class FirelessScenario extends Scenario {

    @Override
    public String getName() {
        return "Fireless";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.FLINT_AND_STEEL)
                .name("&b" + getName())
                .lore(
                        "&7You talk zero damage from fire."
                )
                .build();
    }

    @EventHandler
    public void handleEntityDamageEvent(PlayerDamageEvent event) {
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause == EntityDamageEvent.DamageCause.FIRE || cause == EntityDamageEvent.DamageCause.FIRE_TICK || cause == EntityDamageEvent.DamageCause.LAVA) {
            event.setCancelled(true);
        }
    }
}
