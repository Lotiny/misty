package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XEntity;
import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class VengefulSpiritScenario extends Scenario {

    @Override
    public String getName() {
        return "Vengeful Spirit";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.SKELETON_SKULL)
                .name("&b" + getName())
                .lore(
                        "&7When a player dies, a Charged Creeper spawns",
                        "&7directly on their corpse."
                )
                .build();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) return;

        Location location = victim.getLocation().clone();
        XEntity.spawn(location, Creeper.class, true, creeper -> {
            creeper.setPowered(true);
        });
    }
}
