package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.event.PlayerScatterEvent;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class GoneFishingScenario extends Scenario {

    @Override
    public String getName() {
        return "Gone Fishing";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.COD)
                .name("&b" + getName())
                .lore(
                        "&7Player start with 20000 Level, 64 Anvil",
                        "&7and OP Fishing Rod"
                )
                .build();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerScatter(PlayerScatterEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();

        player.setLevel(20000);
        inventory.addItem(ItemStackUtils.of(XMaterial.ANVIL, 64));
        inventory.addItem(ItemBuilder.of(XMaterial.FISHING_ROD)
                .enchantment(XEnchantment.UNBREAKING, 250)
                .enchantment(XEnchantment.LUCK_OF_THE_SEA, 250)
                .build());
    }
}
