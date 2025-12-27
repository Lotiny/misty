package me.lotiny.misty.bukkit.scenario.impl;

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

public class PuppyPowerScenario extends Scenario {

    @Override
    public String getName() {
        return "Puppy Power";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.BONE)
                .name("&b" + getName())
                .lore(
                        "&7Player start with 64 rotten flesh, 64 bone and",
                        "&764 wolf spawn egg."
                )
                .build();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerScatter(PlayerScatterEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();

        inventory.addItem(ItemStackUtils.of(XMaterial.ROTTEN_FLESH, 64));
        inventory.addItem(ItemStackUtils.of(XMaterial.BONE, 64));
        inventory.addItem(ItemStackUtils.of(XMaterial.WOLF_SPAWN_EGG, 64));
    }
}
