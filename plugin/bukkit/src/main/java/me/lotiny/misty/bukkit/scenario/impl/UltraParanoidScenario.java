package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.Utilities;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class UltraParanoidScenario extends Scenario {

    @Override
    public String getName() {
        return "Ultra Paranoid";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.FIREWORK_ROCKET)
                .name("&b" + getName())
                .lore(
                        "&7Whenever you mined Diamond Ore or Gold Ore",
                        "&7it will send your location to all players."
                )
                .build();
    }

    @EventHandler
    public void handleBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        XMaterial xMaterial = XMaterial.matchXMaterial(block.getType());
        Location location = block.getLocation();

        if (XTag.DIAMOND_ORES.isTagged(xMaterial) || XTag.GOLD_ORES.isTagged(xMaterial)) {
            Utilities.broadcast(Message.ULTRA_PARANOID_BROADCAST.toString()
                    .replace("<player>", player.getName())
                    .replace("<block>", block.getType().toString())
                    .replace("<x>", String.valueOf(location.getBlockX()))
                    .replace("<y>", String.valueOf(location.getBlockY()))
                    .replace("<z>", String.valueOf(location.getBlockZ())));
        }
    }
}
