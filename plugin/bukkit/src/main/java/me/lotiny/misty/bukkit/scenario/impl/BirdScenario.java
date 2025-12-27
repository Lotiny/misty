package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.event.PlayerScatterEvent;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import me.lotiny.misty.bukkit.utils.VersionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

public class BirdScenario extends Scenario {

    @Override
    public String getName() {
        return "Bird";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.ELYTRA)
                .name("&b" + getName())
                .lore(
                        "&7Everyone get the elytra at start!"
                )
                .build();
    }

    @Override
    public boolean shouldRegister() {
        return VersionUtils.isHigher(9, 0);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleScatter(PlayerScatterEvent event) {
        Player player = event.getPlayer();
        player.getInventory().addItem(ItemStackUtils.of(XMaterial.ELYTRA));
    }
}
