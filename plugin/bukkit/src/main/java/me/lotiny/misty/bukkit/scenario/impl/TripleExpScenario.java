package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.ItemStack;

public class TripleExpScenario extends Scenario {

    @Override
    public String getName() {
        return "Triple Exp";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.EXPERIENCE_BOTTLE)
                .name("&b" + getName())
                .lore(
                        "&7The experience you get from everything is tripled."
                )
                .build();
    }

    @EventHandler
    public void handlePlayerExpChange(PlayerExpChangeEvent event) {
        int amount = event.getAmount();
        if (amount > 0) {
            event.setAmount(amount * 3);
        }
    }
}
