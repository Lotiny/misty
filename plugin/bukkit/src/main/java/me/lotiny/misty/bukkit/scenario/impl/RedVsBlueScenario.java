package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.scenario.annotations.IncompatibleWith;
import org.bukkit.inventory.ItemStack;

@IncompatibleWith(LoveAtFirstSightScenario.class)
public class RedVsBlueScenario extends Scenario {

    @Override
    public String getName() {
        return "Red vs Blue";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.BLUE_WOOL)
                .name("&b" + getName())
                .lore(
                        "&7All players will be spit to 2 teams."
                )
                .build();
    }
}
