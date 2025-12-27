package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ZoomiesScenario extends Scenario {

    @Override
    public String getName() {
        return "Zoomies";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.ZOMBIE_HEAD)
                .name("&b" + getName())
                .lore(
                        "&7All mobs will spawn with Speed III effect."
                )
                .build();
    }

    @EventHandler
    public void handleMobSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Monster monster) {
            monster.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, true, true));
        }
    }
}
