package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.event.PlayerScatterEvent;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CreeperPongScenario extends Scenario {

    @Override
    public String getName() {
        return "Creeper Pong";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.CREEPER_SPAWN_EGG)
                .name("&b" + getName())
                .lore(
                        "&7Players start with a stack of Charged Creeper Spawn Eggs,",
                        "&7Knockback X stick and an Unbreaking X Flint and Steel."
                )
                .build();
    }

    @EventHandler
    public void handlePlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            ItemStack item = PlayerUtils.getItemInHand(player);
            if (ItemStackUtils.isNull(item)) return;

            if (item.isSimilar(XMaterial.CREEPER_SPAWN_EGG.parseItem())) {
                if (item.getAmount() == 1) {
                    PlayerUtils.setItemInHand(player, null);
                } else {
                    item.setAmount(item.getAmount() - 1);
                }

                event.setCancelled(true);
                Block block = event.getClickedBlock();
                if (block == null) return;

                Location location = block.getLocation();
                Creeper creeper = (Creeper) location.getWorld().spawnEntity(location.clone().add(0.0, 1.0, 0.0), EntityType.CREEPER);
                creeper.setPowered(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerScatter(PlayerScatterEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();

        inventory.addItem(ItemBuilder.of(XMaterial.CREEPER_SPAWN_EGG)
                .amount(64)
                .data(50)
                .name("&bSpawn Charged Creeper")
                .build());
        inventory.addItem(ItemBuilder.of(XMaterial.STICK)
                .enchantment(XEnchantment.KNOCKBACK, 10)
                .build());
        inventory.addItem(ItemBuilder.of(XMaterial.FLINT_AND_STEEL)
                .enchantment(XEnchantment.UNBREAKING, 10)
                .build());
    }
}
