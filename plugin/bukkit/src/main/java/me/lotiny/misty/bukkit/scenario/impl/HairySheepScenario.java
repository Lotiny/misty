package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;

public class HairySheepScenario extends Scenario {

    @Override
    public String getName() {
        return "Hairy Sheep";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.STRING)
                .name("&b" + getName())
                .lore(
                        "&7Sheep will always drop string when sheared",
                        "&7or died if they didn't sheared."
                )
                .build();
    }

    @EventHandler
    public void handleSheepDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Sheep sheep) {
            if (!sheep.isSheared()) {
                UHCUtils.dropItem(sheep.getLocation(), XMaterial.STRING.parseItem());
            }
        }
    }

    @EventHandler
    public void handleSheepShear(PlayerShearEntityEvent event) {
        if (event.getEntity() instanceof Sheep sheep) {
            UHCUtils.dropItem(sheep.getLocation(), XMaterial.STRING.parseItem());
        }
    }
}
