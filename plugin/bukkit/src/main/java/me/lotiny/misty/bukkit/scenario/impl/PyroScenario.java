package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.event.PlayerScatterEvent;
import me.lotiny.misty.api.scenario.Scenario;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PyroScenario extends Scenario {

    @Override
    public String getName() {
        return "Pyro";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.FIRE_CHARGE)
                .name("&b" + getName())
                .lore(
                        "&7Player start with Fire Aspect and Flame book."
                )
                .build();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerScatter(PlayerScatterEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();

        inventory.addItem(XEnchantment.FIRE_ASPECT.getBook(1));
        inventory.addItem(XEnchantment.FLAME.getBook(1));
    }
}
