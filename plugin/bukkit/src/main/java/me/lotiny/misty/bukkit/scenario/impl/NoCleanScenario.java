package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.events.player.PlayerDamageByPlayerEvent;
import io.fairyproject.bukkit.events.player.PlayerDamageEvent;
import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.metadata.MetadataKey;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.cooldown.PlayerCooldown;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class NoCleanScenario extends Scenario {

    public static MetadataKey<PlayerCooldown> NO_CLEAN_KEY = MetadataKey.create("misty:no-clean-key", PlayerCooldown.class);

    @Override
    public String getName() {
        return "No Clean";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.DIAMOND_SWORD)
                .name("&b" + getName())
                .lore(
                        "&7When you get a kill you will take no damage for 20 seconds."
                )
                .build();
    }

    @EventHandler
    public void handlePlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            PlayerCooldown cooldown = new PlayerCooldown(20_000L);
            cooldown.addCooldown(killer);
            cooldown.removalListener(player -> {
                player.sendMessage(Message.NO_CLEAN_EXPIRED.toString());
                Metadata.provideForPlayer(player).remove(NO_CLEAN_KEY);
            });

            Metadata.provideForPlayer(killer).put(NO_CLEAN_KEY, cooldown);
            killer.sendMessage(Message.NO_CLEAN_APPLIED.toString());
        }
    }

    @EventHandler
    public void handlePlayerDamage(PlayerDamageEvent event) {
        Player player = event.getPlayer();
        PlayerCooldown cooldown = Metadata.provideForPlayer(player).getOrNull(NO_CLEAN_KEY);
        if (cooldown != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handlePlayerDamageByPlayer(PlayerDamageByPlayerEvent event) {
        if (event.isCancelled()) return;

        Player damager = event.getDamager();
        Player damaged = event.getPlayer();

        PlayerCooldown damagedCooldown = Metadata.provideForPlayer(damaged).getOrNull(NO_CLEAN_KEY);
        if (damagedCooldown != null) {
            event.setCancelled(true);
            damager.sendMessage(Message.NO_CLEAN_PROTECTED.toString());
            return;
        }

        PlayerCooldown damagerCooldown = Metadata.provideForPlayer(damager).getOrNull(NO_CLEAN_KEY);
        if (damagerCooldown != null) {
            damagerCooldown.removeCooldown(damager);
            Metadata.provideForPlayer(damager).remove(NO_CLEAN_KEY);
            damager.sendMessage(Message.NO_CLEAN_EXPIRED.toString());
        }
    }
}