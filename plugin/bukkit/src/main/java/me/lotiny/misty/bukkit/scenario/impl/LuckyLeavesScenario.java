package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import io.fairyproject.Fairy;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

public class LuckyLeavesScenario extends Scenario {

    @Override
    public String getName() {
        return "Lucky Leaves";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.OAK_LEAVES)
                .name("&b" + getName())
                .lore(
                        "&7Leaves have a chance to drop a Golden Apple"
                )
                .build();
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (event.isCancelled()) return;

        Block block = event.getBlock();
        if (!XTag.LEAVES.isTagged(XMaterial.matchXMaterial(block.getType()))) return;

        int random = Fairy.random().nextInt(1000);
        if (random < 5) {
            UHCUtils.dropItem(block.getLocation(), XMaterial.GOLDEN_APPLE.parseItem());
        }
    }
}
