package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XEntityType;
import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;

public class FrozenInTimeScenario extends Scenario {

    @Override
    public String getName() {
        return "Frozen In Time";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.ICE)
                .name("&b" + getName())
                .lore(
                        "&7Water and Lava will not flow.",
                        "&7Sand and Gravel will not fall from gravity.",
                        "&7Leaves will not decay."
                )
                .build();
    }

    @EventHandler
    public void handleBlockFromTo(BlockFromToEvent event) {
        Block block = event.getBlock();

        if (block.isLiquid()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handleBlockPhysics(EntityChangeBlockEvent event) {
        if (event.getEntityType() == XEntityType.FALLING_BLOCK.get() && XBlock.isAir(event.getTo())) {
            event.setCancelled(true);
            event.getBlock().getState().update(false, false);
        }
    }
}
