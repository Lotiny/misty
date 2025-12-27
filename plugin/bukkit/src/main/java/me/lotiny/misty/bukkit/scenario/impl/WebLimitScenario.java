package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.enums.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class WebLimitScenario extends Scenario {

    @Override
    public String getName() {
        return "Web Limit";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.COBWEB)
                .name("&b" + getName())
                .lore(
                        "&7Players will be limited to having",
                        "&78 cobwebs in their inventory at one time."
                )
                .build();
    }

    @EventHandler
    public void handlePlayerHeldItem(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();

        ItemStack newItem = inventory.getItem(event.getNewSlot());
        ItemStack prevItem = inventory.getItem(event.getPreviousSlot());

        boolean newIsWeb = isWeb(newItem);
        boolean prevIsWeb = isWeb(prevItem);

        if (!newIsWeb && !prevIsWeb) return;

        int totalWebs = 0;
        ItemStack[] contents = inventory.getContents();
        for (ItemStack item : contents) {
            if (isWeb(item)) {
                totalWebs += item.getAmount();
            }
        }

        int limit = 8;
        if (totalWebs > limit) {
            int keepSlot = newIsWeb ? event.getNewSlot() : event.getPreviousSlot();
            int excess = totalWebs - limit;

            ItemStack keepItem = inventory.getItem(keepSlot);
            if (isWeb(keepItem)) {
                if (keepItem.getAmount() > limit) {
                    excess += (keepItem.getAmount() - limit);
                    keepItem.setAmount(limit);
                }
            }

            for (int i = contents.length - 1; i >= 0 && excess > 0; i--) {
                if (i == keepSlot) continue;

                ItemStack item = contents[i];
                if (isWeb(item)) {
                    int amount = item.getAmount();

                    if (amount <= excess) {
                        inventory.clear(i);
                        excess -= amount;
                    } else {
                        item.setAmount(amount - excess);
                        excess = 0;
                    }
                }
            }

            player.sendMessage(Message.WEB_LIMIT_REACHED.toString());
        }
    }

    private boolean isWeb(ItemStack item) {
        return item != null && XMaterial.COBWEB.isSimilar(item);
    }
}
