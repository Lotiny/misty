package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BetterEnchantScenario extends Scenario {

    @Override
    public String getName() {
        return "Better Enchant";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.ENCHANTED_BOOK)
                .name("&b" + getName())
                .lore(
                        "&7When you left-click the enchanting table",
                        "&7while holding enchanted book the enchant will",
                        "&7be removed from the book!"
                )
                .build();
    }

    @EventHandler
    public void handleInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null || !XBlock.isSimilar(block, XMaterial.ENCHANTING_TABLE)) return;

        Player player = event.getPlayer();
        ItemStack item = PlayerUtils.getItemInHand(player);
        if (item != null && XMaterial.ENCHANTED_BOOK.isSimilar(item)) {
            PlayerUtils.setItemInHand(player, XMaterial.BOOK.parseItem());
            player.sendMessage(Message.BETTER_ENCHANT_USED.toString());
        }
    }
}
