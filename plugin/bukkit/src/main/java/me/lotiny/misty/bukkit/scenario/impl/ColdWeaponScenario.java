package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.scenario.annotations.IncompatibleWith;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

@IncompatibleWith({FirelessScenario.class, PyroScenario.class})
public class ColdWeaponScenario extends Scenario {

    @Override
    public String getName() {
        return "Cold Weapon";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.PACKED_ICE)
                .name("&b" + getName())
                .lore(
                        "&7Flame and Fire Aspect got removed from Enchanting pool",
                        "&7also you cannot apply Flame or Fire Aspect in Anvil."
                )
                .build();
    }

    @EventHandler
    public void handleEnchantItem(EnchantItemEvent event) {
        event.getEnchantsToAdd().remove(XEnchantment.FLAME.get());
        event.getEnchantsToAdd().remove(XEnchantment.FIRE_ASPECT.get());
    }

    @EventHandler
    public void handleAnvilClick(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.ANVIL) {
            ItemStack item = event.getCurrentItem();
            if (item != null && XMaterial.ENCHANTED_BOOK.isSimilar(item)) {
                if (event.getCurrentItem().getEnchantments().containsKey(XEnchantment.FLAME.get()) || event.getCurrentItem().getEnchantments().containsKey(XEnchantment.FIRE_ASPECT.get())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
