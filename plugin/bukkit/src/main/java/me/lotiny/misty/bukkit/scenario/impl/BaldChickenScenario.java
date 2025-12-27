package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class BaldChickenScenario extends Scenario {

    @Override
    public String getName() {
        return "Bald Chicken";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.CHICKEN)
                .name("&b" + getName())
                .lore(
                        "&7Chickens do not drop feathers, instead skeletons",
                        "&7will always drop 3 arrows upon their death."
                )
                .build();
    }

    @EventHandler
    public void handleEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Skeleton) {
            event.getDrops().removeIf(XMaterial.ARROW::isSimilar);
            event.getDrops().add(ItemStackUtils.of(XMaterial.ARROW, 3));
        }
    }
}
