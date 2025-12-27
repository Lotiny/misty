package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.scenario.annotations.IncompatibleWith;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

@IncompatibleWith(AbsorptionPartnerScenario.class)
public class AbsorptionlessScenario extends Scenario {

    @Override
    public String getName() {
        return "Absorptionless";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.GOLDEN_APPLE)
                .name("&b" + getName())
                .lore("&7You cannot get Absorption effect.")
                .build();
    }

    @EventHandler
    public void handlePlayerConsumeEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if (player.hasPotionEffect(PotionEffectType.ABSORPTION)) {
            player.removePotionEffect(PotionEffectType.ABSORPTION);
        }
    }
}
