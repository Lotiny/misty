package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.scenario.annotations.IncompatibleWith;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@IncompatibleWith({GoldlessScenario.class, DiamondlessScenario.class, CustomCraftScenario.class})
public class BarebonesScenario extends Scenario {

    @Override
    public String getName() {
        return "Barebones";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.BONE)
                .name("&b" + getName())
                .lore(
                        "&7You are not able to mined Diamond Ore or Gold Ore!",
                        "&7If you kill a player, they will drop 1 diamond,",
                        "&7a golden apple, 2 string and 32 arrows."
                )
                .build();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleBlockBreakEvent(BlockBreakEvent event) {
        Block block = event.getBlock();
        XMaterial xMaterial = XMaterial.matchXMaterial(block.getType());

        if (XTag.DIAMOND_ORES.isTagged(xMaterial) || XTag.GOLD_ORES.isTagged(xMaterial)) {
            event.setCancelled(true);
            block.setType(Material.AIR);
        }
    }

    @Override
    public List<ItemStack> getDroppedItems() {
        return Arrays.asList(
                ItemStackUtils.of(XMaterial.DIAMOND),
                ItemStackUtils.of(XMaterial.GOLDEN_APPLE),
                ItemStackUtils.of(XMaterial.ARROW, 32),
                ItemStackUtils.of(XMaterial.STRING, 2)
        );
    }
}
