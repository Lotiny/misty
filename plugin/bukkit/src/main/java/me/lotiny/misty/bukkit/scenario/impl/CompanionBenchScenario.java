package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.util.CC;
import me.lotiny.misty.api.event.PlayerScatterEvent;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import me.lotiny.misty.shared.event.PlayerPickupItemEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CompanionBenchScenario extends Scenario {

    private final Map<UUID, String> workBenchLocation = new HashMap<>();

    @Override
    public String getName() {
        return "Companion Bench";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.CRAFTING_TABLE)
                .name("&b" + getName())
                .lore(
                        "&7You will get a Crafting Table at the game start",
                        "&7you cannot craft another crafting table or use",
                        "&7other crafting table."
                )
                .build();
    }

    @EventHandler
    public void handleCraftItem(PrepareItemCraftEvent event) {
        ItemStack item = event.getInventory().getResult();
        if (item == null || item.getType() != XMaterial.CRAFTING_TABLE.get()) return;

        event.getInventory().setResult(XMaterial.OAK_LOG.parseItem());
    }

    @EventHandler
    public void handlePlayerPlaceBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getType() != XMaterial.CRAFTING_TABLE.get()) return;

        this.workBenchLocation.put(player.getUniqueId(), block.getLocation().toString());
    }

    @EventHandler
    public void handlePlayerPickup(PlayerPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();

        if (item.getType() != XMaterial.CRAFTING_TABLE.get()) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void handlePlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        PlayerInventory inventory = player.getInventory();

        if (block.getType() != XMaterial.CRAFTING_TABLE.get()) return;

        if (!this.workBenchLocation.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (!this.workBenchLocation.get(player.getUniqueId()).equals(block.getLocation().toString())) {
            event.setCancelled(true);
            player.sendMessage(CC.RED + "You can't break Crafting Table that isn't your own.");
            return;
        }

        if (inventory.firstEmpty() == -1) {
            event.setCancelled(true);
            player.sendMessage(CC.RED + "Your inventory is full!");
            return;
        }

        event.setCancelled(true);
        block.setType(Material.AIR);
        inventory.addItem(ItemStackUtils.of(XMaterial.CRAFTING_TABLE));
        XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player);
    }

    @EventHandler
    public void handlePlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (event.getItemDrop().getItemStack().getType() != XMaterial.CRAFTING_TABLE.get()) return;

        event.setCancelled(true);
        player.sendMessage(CC.RED + "You can't drop your Crafting Table.");
    }

    @EventHandler
    public void handlePlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            if (block == null) return;

            if (XBlock.isSimilar(block, XMaterial.CRAFTING_TABLE)) {
                if (!this.workBenchLocation.containsKey(player.getUniqueId())) {
                    event.setCancelled(true);
                    player.sendMessage(CC.RED + "You can't use other Crafting Table.");
                    return;
                }

                if (!this.workBenchLocation.get(player.getUniqueId()).equals(block.getLocation().toString())) {
                    event.setCancelled(true);
                    player.sendMessage(CC.RED + "You can't use other Crafting Table.");
                }
            }
        }
    }

    @EventHandler
    public void handleItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        if (XMaterial.CRAFTING_TABLE.isSimilar(item.getItemStack())) {
            item.remove();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerScatter(PlayerScatterEvent event) {
        Player player = event.getPlayer();

        player.getInventory().addItem(ItemStackUtils.of(XMaterial.CRAFTING_TABLE));
    }
}
