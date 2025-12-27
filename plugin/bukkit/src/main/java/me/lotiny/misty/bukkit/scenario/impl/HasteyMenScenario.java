package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import me.lotiny.misty.api.customitem.CustomItemRegistry;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.scenario.annotations.IncompatibleWith;
import me.lotiny.misty.bukkit.utils.scenario.Tools;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

@IncompatibleWith({HasteyBabiesScenario.class, HasteyBoysScenario.class})
public class HasteyMenScenario extends Scenario {

    @Autowired
    private static CustomItemRegistry customItemRegistry;

    @Override
    public String getName() {
        return "Hastey Men";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.DIAMOND_PICKAXE)
                .name("&b" + getName())
                .lore(
                        "&7Every tool that you craft will have",
                        "&7Efficiency 5 and Unbreaking 3."
                )
                .build();
    }

    @EventHandler
    public void handlePrepareCraftEvent(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe == null) return;

        ItemStack result = recipe.getResult();
        if (Tools.isTool(XMaterial.matchXMaterial(result)) && !customItemRegistry.isCustomItem(result)) {
            event.getInventory().setResult(ItemBuilder.of(result.getType())
                    .enchantment(XEnchantment.EFFICIENCY, 5)
                    .enchantment(XEnchantment.UNBREAKING, 3)
                    .build());
        }
    }
}
