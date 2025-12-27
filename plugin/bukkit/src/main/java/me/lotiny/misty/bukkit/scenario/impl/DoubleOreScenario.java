package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.bukkit.scenario.annotations.IncompatibleWith;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import me.lotiny.misty.bukkit.utils.Utilities;
import me.lotiny.misty.bukkit.utils.scenario.OreType;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@IncompatibleWith(TripleOreScenario.class)
public class DoubleOreScenario extends Scenario {

    @Autowired
    private static ScenarioManager scenarioManager;

    @Override
    public String getName() {
        return "Double Ores";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.REDSTONE_ORE)
                .name("&b" + getName())
                .lore(
                        "&7Ores are drop doubled."
                )
                .build();
    }

    @EventHandler
    public void handleBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE || event.isCancelled() || scenarioManager.isEnabled("Cut Clean"))
            return;

        Block block = event.getBlock();
        Location clone = block.getLocation();
        XMaterial xMaterial = XMaterial.matchXMaterial(block.getType());
        Optional<OreType> oreTypeOptional = OreType.get(xMaterial);
        if (oreTypeOptional.isPresent()) {
            OreType oreType = oreTypeOptional.get();
            XMaterial result = oreType.getOutput();

            if (result == oreType.getSmelted()) {
                ItemStack tool = PlayerUtils.getItemInHand(player);
                Enchantment fortune = XEnchantment.FORTUNE.get();
                if (fortune != null && tool.containsEnchantment(fortune)) {
                    int dropAmount = Utilities.getFortuneDrop(xMaterial, tool.getEnchantmentLevel(fortune));
                    UHCUtils.dropItem(clone, ItemStackUtils.of(result, dropAmount * 2));
                }
            } else {
                block.setType(Material.AIR);
                UHCUtils.spawnXpOrb(clone, oreType.getExp());
                UHCUtils.dropItem(clone, ItemStackUtils.of(result, oreType.getBaseAmount() * 2));
            }
        }
    }
}
