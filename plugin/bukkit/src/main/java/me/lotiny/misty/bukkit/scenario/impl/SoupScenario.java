package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SoupScenario extends Scenario {

    @Override
    public String getName() {
        return "Soup";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.MUSHROOM_STEW)
                .name("&b" + getName())
                .lore(
                        "&7Right-click at soup will heal you for 3.5 hearts."
                )
                .build();
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void handlePlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction().toString().startsWith("RIGHT_") && XMaterial.MUSHROOM_STEM.isSimilar(PlayerUtils.getItemInHand(player))) {
            double health = player.getHealth();
            double maxHealth = PlayerUtils.getMaxHealth(player);

            if (health >= maxHealth) return;

            Material bowl = XMaterial.BOWL.get();
            if (bowl == null) return;

            if (health >= maxHealth - 7) {
                player.setHealth(maxHealth);
                PlayerUtils.getItemInHand(player).setType(bowl);
            } else {
                player.setHealth(health + 7);
                PlayerUtils.getItemInHand(player).setType(bowl);
            }
        }
    }
}
