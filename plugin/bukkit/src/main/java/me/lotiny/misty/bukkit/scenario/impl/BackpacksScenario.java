package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import org.bukkit.inventory.ItemStack;

public class BackpacksScenario extends Scenario {

    @Override
    public String getName() {
        return "Backpacks";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.CHEST)
                .name("&b" + getName())
                .lore(
                        "&7Each team will have a shared extra inventory",
                        "&7you can access to it by /bp"
                )
                .build();
    }

}
