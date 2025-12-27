package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.XTag;
import io.fairyproject.Fairy;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ArcaneArchivesScenario extends Scenario {

    @Override
    public String getName() {
        return "Arcane Archives";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.LAPIS_LAZULI)
                .name("&b" + getName())
                .lore(
                        "&7Mining a Lapis Lazuli ore have a chance to drop",
                        "&7a Book or Exp Bottle."
                )
                .build();
    }

    @EventHandler
    public void handleBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!XTag.LAPIS_ORES.isTagged(XMaterial.matchXMaterial(block.getType()))) return;

        Random random = Fairy.random();
        int chance = random.nextInt(100);
        XMaterial material;
        if (chance < 10) {
            if (chance < 5) {
                material = XMaterial.BOOK;
            } else {
                material = XMaterial.EXPERIENCE_BOTTLE;
            }

            PlayerUtils.playSound(player, XSound.ENTITY_EXPERIENCE_ORB_PICKUP);
            UHCUtils.dropItem(block.getLocation(), ItemStackUtils.of(material));
            player.sendMessage(Message.ARCANE_ARCHIVES_DROP.toString()
                    .replace("<item>", material.friendlyName()));
        }
    }
}
