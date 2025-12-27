package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BloodDiamondsScenario extends Scenario {

    @Override
    public String getName() {
        return "Blood Diamonds";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.REDSTONE)
                .name("&b" + getName())
                .lore(
                        "&7You will take 1 damage when mined diamond ore."
                )
                .build();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (XTag.DIAMOND_ORES.isTagged(XMaterial.matchXMaterial(block.getType()))) {
            player.damage(1);
        }
    }
}
