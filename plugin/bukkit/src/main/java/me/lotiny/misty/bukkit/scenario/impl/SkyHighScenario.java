package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bootstrap.bukkit.BukkitPlugin;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import io.fairyproject.util.CC;
import me.lotiny.misty.api.event.BorderShrunkEvent;
import me.lotiny.misty.api.event.PlayerScatterEvent;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;

public class SkyHighScenario extends Scenario {

    @Autowired
    private static GameManager gameManager;

    private BukkitTask task;

    @Override
    public String getName() {
        return "Sky High";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.WATER_BUCKET)
                .name("&b" + getName())
                .lore(
                        "&7Player start with 2 stack of block, 16 pumpkin, 32 snow block",
                        "&7and diamond shovel. When border shrunk to 500x500 player must",
                        "&7go above Y:150 or talking 1 damage every 30 seconds."
                )
                .build();
    }

    @EventHandler
    public void handleBlockBreakEvent(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (XBlock.isSimilar(block, XMaterial.SNOW_BLOCK)) {
            event.setCancelled(true);
            block.setType(Material.AIR);
            block.getState().update();
            UHCUtils.dropItem(block.getLocation(), XMaterial.SNOW_BLOCK.parseItem());
        } else if (XBlock.isSimilar(block, XMaterial.SAND)) {
            event.setCancelled(true);
            block.setType(Material.AIR);
            block.getState().update();
            UHCUtils.dropItem(block.getLocation(), XMaterial.GLASS.parseItem());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerScatter(PlayerScatterEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();

        inventory.addItem(ItemStackUtils.of(XMaterial.BRICK, 64));
        inventory.addItem(ItemStackUtils.of(XMaterial.BRICK, 64));
        inventory.addItem(ItemStackUtils.of(XMaterial.SNOW_BLOCK, 32));
        inventory.addItem(ItemStackUtils.of(XMaterial.PUMPKIN, 16));
        inventory.addItem(ItemStackUtils.of(XMaterial.DIAMOND_SHOVEL));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleBorderShrunk(BorderShrunkEvent event) {
        if (event.getSize() == 500) {
            task = Bukkit.getScheduler().runTaskTimerAsynchronously(BukkitPlugin.INSTANCE, () -> {
                gameManager.getRegistry().getAlivePlayers().forEach(uuid -> {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player.getLocation().getY() <= 150) {
                        player.sendMessage(CC.RED + CC.BOLD + "Please go above Y:150 to prevent this damage.");
                        player.damage(1);
                    }
                });
            }, 0, 600);
        }
    }

    @Override
    public void onDisable() {
        task.cancel();
    }
}
