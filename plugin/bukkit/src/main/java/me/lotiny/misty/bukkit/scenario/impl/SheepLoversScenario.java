package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.Fairy;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

public class SheepLoversScenario extends Scenario {

    @Override
    public String getName() {
        return "Sheep Lovers";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.SHEARS)
                .name("&b" + getName())
                .lore(
                        "&7Shearing sheep have 5% chance to drop gold ingot."
                )
                .build();
    }

    @EventHandler
    public void handleSheepShear(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();

        if (event.getRightClicked() instanceof Sheep sheep &&
                XMaterial.SHEARS.isSimilar(PlayerUtils.getItemInHand(player)) &&
                !sheep.isSheared()) {
            if (Fairy.random().nextInt(100) < 5) {
                UHCUtils.dropItem(sheep.getLocation(), XMaterial.GOLD_INGOT.parseItem());
            }
        }
    }
}