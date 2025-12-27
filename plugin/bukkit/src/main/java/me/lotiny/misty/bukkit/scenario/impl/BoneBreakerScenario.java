package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import io.fairyproject.bukkit.events.player.PlayerDamageEvent;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.scenario.annotations.IncompatibleWith;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

@IncompatibleWith(NoFallScenario.class)
public class BoneBreakerScenario extends Scenario {

    @Override
    public String getName() {
        return "Bone Breaker";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.BONE)
                .name("&b" + getName())
                .lore(
                        "&7If a player takes Fall Damage, they",
                        "&7receive a Slowness IV effect for 30 seconds."
                )
                .build();
    }

    @EventHandler
    public void onPlayerDamage(PlayerDamageEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            double damage = event.getFinalDamage();
            int durationTicks = (int) (damage * 2 * 20);

            PotionEffect effect = XPotion.SLOWNESS.buildPotionEffect(durationTicks, 1);
            if (effect != null) {
                player.addPotionEffect(effect);
            }
        }
    }
}
