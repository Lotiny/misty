package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.Fairy;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EggsScenario extends Scenario {

    @Override
    public String getName() {
        return "Eggs";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.EGG)
                .name("&b" + getName())
                .lore(
                        "&7When you throw an egg any mob/animal in the",
                        "&7game can come out of the egg. Chicken have",
                        "&730% chance to drop an egg."
                )
                .build();
    }

    @EventHandler
    public void handleEggHit(ProjectileHitEvent event) {
        Projectile proj = event.getEntity();

        if (!(proj instanceof Egg)) return;

        List<EntityType> types = new ArrayList<>();

        for (EntityType type : EntityType.values()) {
            if (!type.isAlive() || !type.isSpawnable() || type == EntityType.VILLAGER) {
                continue;
            }

            types.add(type);
        }

        EntityType type = types.get(Fairy.random().nextInt(types.size()));

        Location loc = proj.getLocation();
        World world = proj.getWorld();

        world.spawnEntity(loc, type);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Chicken)) return;

        double chance = 0.3;
        if (Fairy.random().nextDouble() > chance) return;

        event.getDrops().add(XMaterial.EGG.parseItem());
    }
}
