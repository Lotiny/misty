package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.scenario.Scenario;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BedBombScenario extends Scenario {

    @Autowired
    private static GameManager gameManager;

    @Override
    public String getName() {
        return "Bed Bomb";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.RED_BED)
                .name("&b" + getName())
                .lore(
                        "&7Bed will be explode in any world!"
                )
                .build();
    }

    @EventHandler
    public void handleBedInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (!block.getLocation().getWorld().getName().equals(gameManager.getRegistry().getUhcWorld())) return;

        XMaterial xMaterial = XMaterial.matchXMaterial(block.getType());
        if (XTag.BEDS.isTagged(xMaterial)) {
            event.setCancelled(true);
            block.getWorld().createExplosion(block.getLocation(), 4.0f);
        }
    }
}
