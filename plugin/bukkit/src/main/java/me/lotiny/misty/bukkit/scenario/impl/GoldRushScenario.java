package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.scenario.annotations.IncompatibleWith;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

@IncompatibleWith(CustomCraftScenario.class)
public class GoldRushScenario extends Scenario {

    @Override
    public String getName() {
        return "Gold Rush";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.GOLD_INGOT)
                .name("&b" + getName())
                .lore(
                        "&7Leather and Iron armor cannot be craft."
                )
                .build();
    }

    @EventHandler
    public void handlePlayerCraft(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe == null) return;

        XMaterial xMaterial = XMaterial.matchXMaterial(recipe.getResult());
        if (XTag.LEATHER_ARMOR_PIECES.isTagged(xMaterial) || XTag.IRON_ARMOR_PIECES.isTagged(xMaterial)) {
            event.getInventory().setResult(XMaterial.AIR.parseItem());
        }
    }
}
