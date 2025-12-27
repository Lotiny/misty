package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XEntityType;
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
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

@IncompatibleWith(RandomizerScenario.class)
public class CutCleanScenario extends Scenario {

    @Autowired
    private static ScenarioManager scenarioManager;

    @Override
    public String getName() {
        return "Cut Clean";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.IRON_INGOT)
                .name("&b" + getName())
                .lore(
                        "&7All ores and animal food will be",
                        "&7dropped in it's smelted version."
                )
                .build();
    }

    @EventHandler
    public void handleBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (event.isCancelled() || player.getGameMode() == GameMode.CREATIVE) return;

        Location location = block.getLocation();
        if (XBlock.isSimilar(block, XMaterial.GRAVEL)) {
            block.setType(Material.AIR);
            UHCUtils.dropItem(location, XMaterial.FLINT.parseItem());
            return;
        }

        XMaterial xMaterial = XMaterial.matchXMaterial(block.getType());
        OreType.get(xMaterial).ifPresent(oreType -> {
            XMaterial result = oreType.getSmelted();
            int multiply = getOreMultiplier();

            if (result != oreType.getOutput()) {
                event.setExpToDrop(0);
                block.setType(Material.AIR);

                UHCUtils.spawnXpOrb(location, oreType.getExp());
                UHCUtils.dropItem(location, ItemStackUtils.of(result, oreType.getBaseAmount() * multiply));
            } else {
                ItemStack tool = PlayerUtils.getItemInHand(player);
                Enchantment fortune = XEnchantment.FORTUNE.get();
                if (fortune != null && tool.containsEnchantment(fortune)) {
                    int dropAmount = Utilities.getFortuneDrop(xMaterial, tool.getEnchantmentLevel(fortune));
                    UHCUtils.dropItem(location, ItemStackUtils.of(result, dropAmount * multiply));
                }
            }
        });
    }

    private int getOreMultiplier() {
        if (scenarioManager.isEnabled("Double Ores")) {
            return 2;
        } else if (scenarioManager.isEnabled("Triple Ores")) {
            return 3;
        }
        return 1;
    }

    @EventHandler
    public void handleEntityDeathEvent(EntityDeathEvent event) {
        XEntityType xEntityType = XEntityType.of(event.getEntityType());
        switch (xEntityType) {
            case COW:
            case MOOSHROOM:
                event.getDrops().clear();
                event.getDrops().add(ItemStackUtils.of(XMaterial.LEATHER));
                event.getDrops().add(ItemStackUtils.of(XMaterial.COOKED_BEEF, 3));
                break;
            case PIG:
                event.getDrops().clear();
                event.getDrops().add(ItemStackUtils.of(XMaterial.COOKED_PORKCHOP, 3));
                break;
            case CHICKEN:
                event.getDrops().clear();
                event.getDrops().add(ItemStackUtils.of(XMaterial.COOKED_CHICKEN, 1));
                if (!scenarioManager.isEnabled("Bald Chicken")) {
                    event.getDrops().add(ItemStackUtils.of(XMaterial.FEATHER, 1));
                }
                break;
            case HORSE:
                event.getDrops().clear();
                event.getDrops().add(ItemStackUtils.of(XMaterial.LEATHER, 1));
                break;
            case SHEEP:
                event.getDrops().clear();
                Sheep sheep = (Sheep) event.getEntity();
                if (!sheep.isSheared()) {
                    event.getDrops().add(ItemStackUtils.of(XMaterial.WHITE_WOOL, 1));
                }

                if (!XMaterial.COOKED_MUTTON.isSupported()) {
                    event.getDrops().add(ItemStackUtils.of(XMaterial.COOKED_BEEF, 3));
                } else {
                    event.getDrops().add(ItemStackUtils.of(XMaterial.COOKED_MUTTON, 3));
                }
                break;
        }
    }
}
