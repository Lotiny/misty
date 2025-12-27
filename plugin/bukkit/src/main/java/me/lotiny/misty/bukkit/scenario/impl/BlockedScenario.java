package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.metadata.MetadataKey;
import io.fairyproject.metadata.MetadataMap;
import io.fairyproject.util.CC;
import me.lotiny.misty.api.scenario.Scenario;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class BlockedScenario extends Scenario {

    private final MetadataKey<UUID> PLACED_BLOCK_KEY = MetadataKey.createUuidKey("misty:blocked");

    @Override
    public String getName() {
        return "Blocked";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.BRICKS)
                .name("&b" + getName())
                .lore(
                        "&7You cannot break block that you placed!"
                )
                .build();
    }

    @EventHandler
    public void handleBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        Metadata.provideForBlock(block).put(PLACED_BLOCK_KEY, player.getUniqueId());
    }

    @EventHandler
    public void handleBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        MetadataMap meta = Metadata.provideForBlock(block);
        if (meta.has(PLACED_BLOCK_KEY) && meta.getOrThrow(PLACED_BLOCK_KEY).equals(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(CC.RED + "Blocked is enabled, You cannot break blocks that you place.");
        }
    }
}
