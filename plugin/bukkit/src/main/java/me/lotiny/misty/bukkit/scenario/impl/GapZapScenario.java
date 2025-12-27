package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.events.player.PlayerDamageEvent;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.util.CC;
import me.lotiny.misty.api.scenario.Scenario;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class GapZapScenario extends Scenario {

    @Override
    public String getName() {
        return "Gap Zap";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.GOLD_NUGGET)
                .name("&b" + getName())
                .lore(
                        "&7When you take damage while you have regeneration",
                        "&7effect the effect will be cancel."
                )
                .build();
    }

    @EventHandler
    public void handlePlayerDamaged(PlayerDamageEvent event) {
        Player player = event.getPlayer();
        if (player.hasPotionEffect(PotionEffectType.REGENERATION)) {
            player.removePotionEffect(PotionEffectType.REGENERATION);

            player.sendMessage(CC.RED + "You have lose your regeneration effect because you taking a damage.");
        }
    }
}
