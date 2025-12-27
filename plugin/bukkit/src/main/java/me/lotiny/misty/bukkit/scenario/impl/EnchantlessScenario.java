package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.util.CC;
import me.lotiny.misty.api.scenario.Scenario;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EnchantlessScenario extends Scenario {

    @Override
    public String getName() {
        return "Enchantless";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.ENCHANTING_TABLE)
                .name("&b" + getName())
                .lore(
                        "&7Enchantment Table and Anvil are disabled!"
                )
                .build();
    }

    @EventHandler
    public void handlePlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();

        if (action == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block == null) return;

            if (XBlock.isSimilar(block, XMaterial.ANVIL) || XBlock.isSimilar(block, XMaterial.CHIPPED_ANVIL) ||
                    XBlock.isSimilar(block, XMaterial.DAMAGED_ANVIL) || XBlock.isSimilar(block, XMaterial.ENCHANTING_TABLE)) {
                event.setCancelled(true);
                player.sendMessage(CC.RED + "You can't use this while Enchantless Scenario is enabled.");
            }
        }
    }
}
