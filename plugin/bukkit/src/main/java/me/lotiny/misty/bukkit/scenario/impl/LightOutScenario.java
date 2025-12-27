package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.enums.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class LightOutScenario extends Scenario {

    @Override
    public String getName() {
        return "Light Out";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.TORCH)
                .name("&b" + getName())
                .lore(
                        "&7Torch cannot be placed."
                )
                .build();
    }

    @EventHandler
    public void handlePlayerBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (XBlock.isSimilar(event.getBlock(), XMaterial.TORCH)) {
            event.setCancelled(true);
            player.sendMessage(Message.SCENARIO_BLOCK_ACTION.toString()
                    .replace("<scenario>", this.getName()));
        }
    }
}
