package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.metadata.MetadataKey;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.utils.scenario.Coffin;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

public class TimebombScenario extends Scenario {

    public static MetadataKey<Location> CHEST_KEY = MetadataKey.create("misty:timebomb-chest-key", Location.class);

    @Override
    public String getName() {
        return "Timebomb";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.TNT)
                .name("&b" + getName())
                .lore(
                        "&7When player died their stuff will be in the chest",
                        "&7that have 30 seconds to loot it."
                )
                .build();
    }

    @EventHandler
    public void handlePlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        ItemStack[] contents = Arrays.stream(victim.getInventory().getContents())
                .filter(Objects::nonNull)
                .toArray(ItemStack[]::new);

        ItemStack[] armor = Arrays.stream(victim.getInventory().getArmorContents())
                .filter(Objects::nonNull)
                .toArray(ItemStack[]::new);

        Coffin coffin = new Coffin(victim, killer, contents, armor);
        coffin.spawn(victim.getLocation());

        event.getDrops().clear();
    }

    @EventHandler
    public void handleBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (XBlock.isSimilar(block, XMaterial.CHEST)) {
            Metadata.provideForBlock(block).ifPresent(CHEST_KEY, location -> event.setCancelled(true));
        }
    }
}
