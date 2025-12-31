package me.lotiny.misty.bukkit.listener;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import io.fairyproject.bukkit.events.BukkitEventFilter;
import io.fairyproject.bukkit.events.BukkitEventNode;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.container.PreDestroy;
import io.fairyproject.event.EventNode;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.bukkit.config.ConfigManager;
import me.lotiny.misty.bukkit.config.impl.MainConfig;
import me.lotiny.misty.bukkit.utils.GoldenHead;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

@InjectableComponent
@RequiredArgsConstructor
public class ConsumableItemHandler {

    private final BukkitEventNode globalNode;
    private final ConfigManager configManager;

    private MainConfig.Healing.HealingItem goldenApple;
    private MainConfig.Healing.HealingItem playerHead;
    private MainConfig.Healing.HealingItem goldenHead;

    private EventNode<BlockEvent> blockNode;
    private EventNode<PlayerEvent> playerNode;

    @PostInitialize
    public void onPostInit() {
        if (VersionUtils.isHigher(21, 4)) {
            this.blockNode = EventNode.type(
                    "misty-consumable-item-blocks",
                    BukkitEventFilter.BLOCK
            );

            blockNode.addListener(BlockPlaceEvent.class, event -> {
                ItemStack item = event.getItemInHand();

                if (GoldenHead.build().isSimilar(item) || XMaterial.PLAYER_HEAD.isSimilar(item)) {
                    event.setCancelled(true);
                }
            });

            globalNode.addChild(blockNode);
        } else {
            this.playerNode = EventNode.type(
                    "misty-consumable-item-players",
                    BukkitEventFilter.PLAYER
            );

            MainConfig.Healing healing = configManager.get(MainConfig.class).getHealing();
            this.goldenApple = healing.getGoldenApple();
            this.playerHead = healing.getPlayerHead();
            this.goldenHead = healing.getGoldenHead();

            playerNode.addListener(PlayerItemConsumeEvent.class, event -> {
                Player player = event.getPlayer();
                ItemStack item = event.getItem();

                MainConfig.Healing.HealingItem healingItem = getHealingItem(item);
                if (healingItem == null || !healingItem.isEnabled()) return;

                applyEffects(player, healingItem.getPotionEffects());
            });

            playerNode.addListener(PlayerInteractEvent.class, event -> {
                Player player = event.getPlayer();
                ItemStack item = event.getItem();

                MainConfig.Healing.HealingItem healingItem = getHealingItem(item);
                if (healingItem == null || !isInstantConsume(healingItem)) return;

                PlayerItemConsumeEvent consumeEvent;
                if (VersionUtils.isHigher(21, 0)) {
                    //noinspection UnstableApiUsage
                    consumeEvent = new PlayerItemConsumeEvent(player, item, event.getHand() == null ? EquipmentSlot.HAND : event.getHand());
                } else {
                    //noinspection removal,UnstableApiUsage
                    consumeEvent = new PlayerItemConsumeEvent(player, item);
                }

                Bukkit.getPluginManager().callEvent(consumeEvent);

                if (consumeEvent.isCancelled()) return;

                consumeItem(player, item);
                PlayerUtils.playSound(
                        player.getLocation(),
                        XSound.ENTITY_PLAYER_BURP,
                        XSound.ENTITY_GENERIC_EAT
                );
            });

            globalNode.addChild(playerNode);
        }
    }

    @PreDestroy
    public void onPreDestroy() {
        if (blockNode != null) {
            globalNode.removeChild(blockNode);
        }

        if (playerNode != null) {
            globalNode.removeChild(playerNode);
        }
    }

    private boolean isInstantConsume(MainConfig.Healing.HealingItem heal) {
        return heal.getTime() == 0.0;
    }

    private MainConfig.Healing.HealingItem getHealingItem(ItemStack item) {
        if (item == null) {
            return null;
        }

        if (goldenHead.isEnabled() && GoldenHead.build().isSimilar(item)) {
            return goldenHead;
        }

        if (goldenApple.isEnabled() && XMaterial.GOLDEN_APPLE.isSimilar(item)) {
            return goldenApple;
        }

        if (playerHead.isEnabled() && XMaterial.PLAYER_HEAD.isSimilar(item)) {
            return playerHead;
        }

        return null;
    }

    private void consumeItem(Player player, ItemStack item) {
        if (item.getAmount() <= 1) {
            PlayerUtils.setItemInHand(player, null);
        } else {
            item.setAmount(item.getAmount() - 1);
            PlayerUtils.setItemInHand(player, item);
        }
    }

    private void applyEffects(Player player, Collection<PotionEffect> effects) {
        for (PotionEffect effect : effects) {
            player.removePotionEffect(effect.getType());
            player.addPotionEffect(effect);
        }
    }
}
