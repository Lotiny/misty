package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import io.fairyproject.metadata.MetadataKey;
import me.lotiny.misty.api.event.UHCMinuteEvent;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.MaterialUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class CentralEnchantmentScenario extends Scenario {

    @Autowired
    private static GameManager gameManager;

    private final MetadataKey<Boolean> ENCHANT_KEY = MetadataKey.create("misty:enchant-key", Boolean.class);

    @Override
    public String getName() {
        return "Central Enchantment";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.ENCHANTING_TABLE)
                .name("&b" + getName())
                .lore(
                        "&7Players cannot craft enchanting tables, but",
                        "&7a single, pre-placed enchanting table exists at",
                        "&7 the world center (0,0). This creates a high-risk,",
                        "&7high-reward area that teams must fight to control."
                )
                .build();
    }

    @EventHandler
    public void handleMinutes(UHCMinuteEvent event) {
        int minute = event.getMinutes();
        if (minute != 1) return;

        World world = Bukkit.getWorld(gameManager.getRegistry().getUhcWorld());
        if (world == null) return;

        Location center = new Location(world, 0, 0, 0);
        int highestY = world.getHighestBlockYAt(center);

        Location tableLocation = new Location(world, 0, highestY + 2, 0);
        Block block = tableLocation.getBlock();
        block.setType(MaterialUtils.getMaterial(XMaterial.ENCHANTING_TABLE));

        Metadata.provideForBlock(block).put(ENCHANT_KEY, true);
    }

    @EventHandler
    public void handleCrafting(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe == null) return;

        if (recipe.getResult().isSimilar(XMaterial.ENCHANTING_TABLE.parseItem())) {
            event.getInventory().setResult(XMaterial.AIR.parseItem());
        }
    }

    @EventHandler
    public void handleBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (XMaterial.matchXMaterial(block.getType()) != XMaterial.ENCHANTING_TABLE) return;

        boolean enchant = Metadata.provideForBlock(block).getOrDefault(ENCHANT_KEY, false);
        if (enchant) {
            event.setCancelled(true);
            player.sendMessage(Message.SCENARIO_BLOCK_ACTION.toString()
                    .replace("<scenario>", this.getName()));
        }
    }

    @EventHandler
    public void handleInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (XMaterial.matchXMaterial(block.getType()) != XMaterial.ENCHANTING_TABLE) return;

        boolean enchant = Metadata.provideForBlock(block).getOrDefault(ENCHANT_KEY, false);
        if (!enchant) {
            event.setCancelled(true);
            player.sendMessage(Message.SCENARIO_BLOCK_ACTION.toString()
                    .replace("<scenario>", this.getName()));
        }
    }
}
