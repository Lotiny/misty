package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.event.PlayerScatterEvent;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

public class DoubleHealthScenario extends Scenario {

    @Override
    public String getName() {
        return "Double Health";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.GOLDEN_APPLE)
                .name("&b" + getName())
                .lore(
                        "&7Everyone will start with 20 hearts!"
                )
                .build();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleScatter(PlayerScatterEvent event) {
        Player player = event.getPlayer();

        PlayerUtils.setMaxHealth(player, PlayerUtils.getMaxHealth(player) + 20);
        PlayerUtils.healPlayer(player);
    }
}
