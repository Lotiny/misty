package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class BleedingSweetsScenario extends Scenario {

    @Override
    public String getName() {
        return "Bleeding Sweets";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.DIAMOND)
                .name("&b" + getName())
                .lore(
                        "&7Whenever a player dies, they will drop a diamond,",
                        "&75 gold, 1 string, 16 arrows."
                )
                .build();
    }

    @Override
    public List<ItemStack> getDroppedItems() {
        return Arrays.asList(
                ItemStackUtils.of(XMaterial.DIAMOND),
                ItemStackUtils.of(XMaterial.GOLD_INGOT, 5),
                ItemStackUtils.of(XMaterial.ARROW, 16),
                ItemStackUtils.of(XMaterial.STRING)
        );
    }
}
