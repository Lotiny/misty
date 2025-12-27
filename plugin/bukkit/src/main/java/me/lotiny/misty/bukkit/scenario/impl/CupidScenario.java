package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.events.player.PlayerDamageByPlayerEvent;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class CupidScenario extends Scenario {

    @Override
    public String getName() {
        return "Cupid";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.BOW)
                .name("&b" + getName())
                .lore(
                        "&7Every time you shoot and hit a player with your",
                        "&7bow, you gain 1% of your health back."
                )
                .build();
    }

    @EventHandler
    public void handlePlayerBow(PlayerDamageByPlayerEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) return;

        Player shooter = event.getDamager();
        double heal = PlayerUtils.getMaxHealth(shooter) * 0.1;
        if (shooter.getHealth() < PlayerUtils.getMaxHealth(shooter)) {
            shooter.setHealth(Math.min(shooter.getHealth() + heal, PlayerUtils.getMaxHealth(shooter)));
        }
    }
}
