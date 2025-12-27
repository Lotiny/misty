package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.scenario.annotations.IncompatibleWith;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import me.lotiny.misty.bukkit.utils.MaterialUtils;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import me.lotiny.misty.bukkit.utils.scenario.RandomizerUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

@IncompatibleWith({FlowerPowerScenario.class, CutCleanScenario.class})
public class RandomizerScenario extends Scenario {

    private RandomizerUtils utils;

    @Override
    public String getName() {
        return "Randomizer";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.GRASS_BLOCK)
                .name("&b" + getName())
                .lore(
                        "&7Every block you break or entity you kill",
                        "&7have a randomly partner drop."
                )
                .build();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        Block block = event.getBlock();
        Block blockBelow = block.getRelative(BlockFace.DOWN);
        Block blockToBreak = MaterialUtils.isDoublePlant(blockBelow) ? blockBelow : block;
        Location location = blockToBreak.getLocation();
        Object object = utils.getDropItem(blockToBreak);

        blockToBreak.setType(Material.AIR);

        if (object instanceof XMaterial xMat) {
            UHCUtils.dropItem(location, ItemStackUtils.of(xMat));
        } else {
            Collection<ItemStack> drops = RandomizerUtils.get().getLootTableHelper().getLootItems(location, event.getPlayer(), (int) object);
            for (ItemStack drop : drops) {
                UHCUtils.dropItem(location, drop);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        EntityType entityType = event.getEntityType();
        ItemStack drop = utils.getDropItem(entityType);
        event.getDrops().clear();
        event.getDrops().add(drop);
    }

    @Override
    public void onEnable() {
        utils = RandomizerUtils.get();
    }
}
