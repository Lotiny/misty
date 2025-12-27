package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedDemonScenario extends Scenario {

    @Override
    public String getName() {
        return "Speed Demon";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.SUGAR)
                .name("&b" + getName())
                .lore(
                        "&7When player get a kill they will get speed effect",
                        "&7and get higher speed level when get another kill."
                )
                .build();
    }

    @EventHandler
    public void handlePlayerDeath(PlayerDeathEvent event) {
        int speedLevel = -1;

        Player victim = event.getEntity();
        if (victim.getKiller() != null) {
            Player killer = victim.getKiller();
            for (PotionEffect effects : killer.getActivePotionEffects()) {
                if (effects.getType() == PotionEffectType.SPEED) {
                    speedLevel = effects.getAmplifier();
                    break;
                }
            }

            if (speedLevel == -1) {
                killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
            } else {
                killer.removePotionEffect(PotionEffectType.SPEED);
                killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, speedLevel + 1));
            }
        }
    }
}
