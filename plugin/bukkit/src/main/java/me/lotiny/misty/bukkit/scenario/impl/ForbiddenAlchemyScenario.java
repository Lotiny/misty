package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.XTag;
import io.fairyproject.Fairy;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Random;

public class ForbiddenAlchemyScenario extends Scenario {

    private final int chance;
    private final Map<XMaterial, Integer> brewingDrops;

    public ForbiddenAlchemyScenario() {
        chance = Config.getScenarioConfig().getForbiddenAlchemy().getChance();
        brewingDrops = Config.getScenarioConfig().getForbiddenAlchemy().getIngredients();
    }

    @Override
    public String getName() {
        return "Forbidden Alchemy";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.BREWING_STAND)
                .name("&b" + getName())
                .lore(
                        "&7Mining a Redstone Ore has a chance to drop",
                        "&7a brewing ingredient."
                )
                .build();
    }

    @EventHandler
    public void handleBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!XTag.REDSTONE_ORES.isTagged(XMaterial.matchXMaterial(block.getType()))) return;

        Random random = Fairy.random();
        int chance = random.nextInt(100);

        if (chance < this.chance) {
            XMaterial drop = getWeightedRandomDrop(random);
            if (drop != null && drop.isSupported()) {
                UHCUtils.dropItem(block.getLocation(), ItemStackUtils.of(drop));
                PlayerUtils.playSound(XSound.ENTITY_EXPERIENCE_ORB_PICKUP);
                player.sendMessage(Message.FORBIDDEN_ALCHEMY_DROP.toString()
                        .replace("<item>", drop.friendlyName()));
            }
        }
    }

    private XMaterial getWeightedRandomDrop(Random random) {
        int totalWeight = brewingDrops.values().stream().mapToInt(Integer::intValue).sum();
        int roll = random.nextInt(totalWeight);
        int cumulative = 0;

        for (Map.Entry<XMaterial, Integer> entry : brewingDrops.entrySet()) {
            cumulative += entry.getValue();
            if (roll < cumulative) {
                return entry.getKey();
            }
        }
        return null;
    }
}
