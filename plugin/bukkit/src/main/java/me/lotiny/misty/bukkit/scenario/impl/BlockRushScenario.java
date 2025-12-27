package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class BlockRushScenario extends Scenario {

    private final Set<XMaterial> minedBlocks = new HashSet<>();

    @Override
    public String getName() {
        return "Block Rush";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.BRICK_STAIRS)
                .name("&b" + getName())
                .lore(
                        "&7If you are the first person to mined new",
                        "&7block you will revived 1 gold ingot."
                )
                .build();
    }

    @EventHandler
    public void handlePlayerBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        XMaterial xMaterial = XMaterial.matchXMaterial(block.getType());
        if (!minedBlocks.contains(xMaterial)) {
            Player player = event.getPlayer();

            minedBlocks.add(xMaterial);

            UHCUtils.dropItem(block.getLocation(), XMaterial.GOLD_INGOT.parseItem());
            player.sendMessage(Message.BLOCK_RUSH_FIRST.toString()
                    .replace("<block>", block.getType().toString()));
        }
    }
}
