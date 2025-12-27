package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.scenario.annotations.IncompatibleWith;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

@IncompatibleWith(BarebonesScenario.class)
public class DiamondlessScenario extends Scenario {

    @Override
    public String getName() {
        return "Diamondless";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.DIAMOND)
                .name("&b" + getName())
                .lore(
                        "&7You cannot mined diamond ore player",
                        "&7will drop 1 diamond when they died."
                )
                .build();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleBlockBreakEvent(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (XTag.DIAMOND_ORES.isTagged(XMaterial.matchXMaterial(block.getType()))) {
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
        }
    }

    @Override
    public List<ItemStack> getDroppedItems() {
        return Collections.singletonList(XMaterial.DIAMOND.parseItem());
    }
}
