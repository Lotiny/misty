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
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

@InjectableComponent
@RequiredArgsConstructor
public class ConsumableItemListener {

    private final ConfigManager configManager;
    private final BukkitEventNode globalNode;

    private MainConfig.Healing.HealingItem goldenApple;
    private MainConfig.Healing.HealingItem playerHead;
    private MainConfig.Healing.HealingItem goldenHead;

    private EventNode<Event> eventNode;

    @SuppressWarnings("deprecation")
    @PostInitialize
    public void onPostInit() {
        this.eventNode = EventNode.type("consumable-item-listeners", BukkitEventFilter.ALL);

        if (VersionUtils.isHigher(21, 4)) {
            eventNode.addListener(BlockPlaceEvent.class, event -> {
                ItemStack item = event.getItemInHand();

                if (GoldenHead.getItem().isSimilar(item) || XMaterial.PLAYER_HEAD.isSimilar(item)) {
                    event.setCancelled(true);
                }
            });
        } else {
            MainConfig.Healing healing = configManager.get(MainConfig.class).getHealing();
            this.goldenApple = healing.getGoldenApple();
            this.playerHead = healing.getPlayerHead();
            this.goldenHead = healing.getGoldenHead();

            eventNode.addListener(PlayerItemConsumeEvent.class, event -> {
                Player player = event.getPlayer();
                ItemStack item = event.getItem();

                MainConfig.Healing.HealingItem healingItem = getHealingItem(item);
                if (healingItem == null || !healingItem.isEnabled()) return;

                applyEffects(player, healingItem.getPotionEffects());
            });

            eventNode.addListener(PlayerInteractEvent.class, event -> {
                Player player = event.getPlayer();
                ItemStack item = event.getItem();

                MainConfig.Healing.HealingItem healingItem = getHealingItem(item);
                if (healingItem == null || !isInstantConsume(healingItem)) return;

                PlayerItemConsumeEvent consumeEvent;
                if (VersionUtils.isHigher(21, 0)) {
                    consumeEvent = new PlayerItemConsumeEvent(
                            player, item, event.getHand() == null ? EquipmentSlot.HAND : event.getHand());
                } else {
                    consumeEvent = new PlayerItemConsumeEvent(player, item);
                }

                Bukkit.getPluginManager().callEvent(consumeEvent);

                if (consumeEvent.isCancelled()) return;

                consumeItem(player, item);
                PlayerUtils.playSound(player.getLocation(), XSound.ENTITY_PLAYER_BURP, XSound.ENTITY_GENERIC_EAT);
            });
        }

        globalNode.addChild(eventNode);
    }

    @PreDestroy
    public void onPreDestroy() {
        globalNode.removeChild(eventNode);
    }

    private boolean isInstantConsume(MainConfig.Healing.HealingItem heal) {
        return heal.getTime() == 0.0;
    }

    private MainConfig.Healing.HealingItem getHealingItem(ItemStack item) {
        if (item == null) {
            return null;
        }

        if (goldenHead.isEnabled() && GoldenHead.getItem().isSimilar(item)) {
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
