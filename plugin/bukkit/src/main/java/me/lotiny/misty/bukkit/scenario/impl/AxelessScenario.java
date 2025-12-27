package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.VersionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AxelessScenario extends Scenario {

    @Override
    public String getName() {
        return "Axeless";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.IRON_AXE)
                .name("&b" + getName())
                .lore(
                        "&7Axe cannot be used."
                )
                .build();
    }

    @EventHandler
    public void handlePlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (XTag.AXES.isTagged(XMaterial.matchXMaterial(PlayerUtils.getItemInHand(player)))) {
            PlayerUtils.setItemInHand(player, null);
        }

        if (VersionUtils.isHigher(9, 0) && XTag.AXES.isTagged(XMaterial.matchXMaterial(PlayerUtils.getItemInOffHand(player)))) {
            PlayerUtils.setItemInOffHand(player, null);
        }
    }
}
