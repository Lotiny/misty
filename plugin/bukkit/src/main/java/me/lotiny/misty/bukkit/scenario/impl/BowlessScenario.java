package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.scenario.annotations.IncompatibleWith;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.VersionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@IncompatibleWith(SwitcherooScenario.class)
public class BowlessScenario extends Scenario {

    @Override
    public String getName() {
        return "Bowless";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.BOW)
                .name("&b" + getName())
                .lore(
                        "&7Bow cannot be used."
                )
                .build();
    }

    @EventHandler
    public void handlePlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (XMaterial.BOW.isSimilar(PlayerUtils.getItemInHand(player))) {
            PlayerUtils.setItemInHand(player, null);
        }

        if (VersionUtils.isHigher(9, 0) && XMaterial.BOW.isSimilar(PlayerUtils.getItemInOffHand(player))) {
            PlayerUtils.setItemInOffHand(player, null);
        }
    }
}
