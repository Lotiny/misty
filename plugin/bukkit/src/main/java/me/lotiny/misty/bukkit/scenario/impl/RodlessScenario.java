package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.VersionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RodlessScenario extends Scenario {

    @Override
    public String getName() {
        return "Rodless";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.FISHING_ROD)
                .name("&b" + getName())
                .lore(
                        "&7Fishing Rod cannot be used."
                )
                .build();
    }

    @EventHandler
    public void handlePlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (XMaterial.FISHING_ROD.isSimilar(PlayerUtils.getItemInHand(player))) {
            PlayerUtils.setItemInHand(player, null);
        }

        if (VersionUtils.isHigher(9, 0) && XMaterial.FISHING_ROD.isSimilar(PlayerUtils.getItemInOffHand(player))) {
            PlayerUtils.setItemInOffHand(player, null);
        }
    }
}
