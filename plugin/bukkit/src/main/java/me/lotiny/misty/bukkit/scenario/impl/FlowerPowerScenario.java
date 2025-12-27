package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.scenario.annotations.IncompatibleWith;
import me.lotiny.misty.bukkit.utils.MaterialUtils;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import me.lotiny.misty.bukkit.utils.scenario.FlowerPowerUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

@IncompatibleWith(RandomizerScenario.class)
public class FlowerPowerScenario extends Scenario {

    private FlowerPowerUtils utils;

    @Override
    public String getName() {
        return "Flower Power";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.POPPY)
                .name("&b" + getName())
                .lore(
                        "&7Item will drop randomly when break the flower."
                )
                .build();
    }

    @EventHandler
    public void handleBlockBreakEvent(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        Block block = event.getBlock();
        Block blockBelow = block.getRelative(BlockFace.DOWN);
        Block blockToBreak = MaterialUtils.isFlower(blockBelow) ? blockBelow : block;
        if (MaterialUtils.isFlower(block) && MaterialUtils.isFlower(blockToBreak)) {
            Location location = blockToBreak.getLocation();
            blockToBreak.setType(Material.AIR);

            int dropXp = utils.getDropXp();
            if (dropXp > 0) {
                UHCUtils.spawnXpOrb(location, dropXp);
            }

            ItemStack drop = utils.getRandomDrop();
            UHCUtils.dropItem(location, drop);
        }
    }

    @Override
    public void onEnable() {
        utils = FlowerPowerUtils.get();
    }
}
