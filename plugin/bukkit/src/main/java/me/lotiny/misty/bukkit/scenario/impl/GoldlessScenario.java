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

import java.util.Collections;
import java.util.List;

@IncompatibleWith(BarebonesScenario.class)
public class GoldlessScenario extends Scenario {

    @Override
    public String getName() {
        return "Goldless";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.GOLD_ORE)
                .name("&b" + getName())
                .lore(
                        "&7You cannot mined gold ore player",
                        "&7will drop 1 gold when they died."
                )
                .build();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleBlockBreakEvent(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (XTag.GOLD_ORES.isTagged(XMaterial.matchXMaterial(block.getType()))) {
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
        }
    }

    @Override
    public List<ItemStack> getDroppedItems() {
        return Collections.singletonList(ItemStackUtils.of(XMaterial.GOLD_INGOT, 8));
    }
}
