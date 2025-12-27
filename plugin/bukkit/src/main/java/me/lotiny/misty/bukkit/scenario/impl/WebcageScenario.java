package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.mc.scheduler.MCSchedulers;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.utils.MaterialUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class WebcageScenario extends Scenario {

    @Override
    public String getName() {
        return "Webcage";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.COBWEB)
                .name("&b" + getName())
                .lore(
                        "&7Whenever a player dies, a sphere of cobwebs",
                        "&7will spawn on where the player has died."
                )
                .build();
    }

    @EventHandler
    public void handlePlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location center = player.getLocation();

        MCSchedulers.getAsyncScheduler().schedule(() -> {
            List<Location> sphereLocations = getSphere(center);

            MCSchedulers.getGlobalScheduler().schedule(() -> {
                for (Location loc : sphereLocations) {
                    if (loc.getBlock().getType() == Material.AIR) {
                        loc.getBlock().setType(MaterialUtils.getMaterial(XMaterial.COBWEB));
                    }
                }
            });
        });
    }

    private List<Location> getSphere(Location centerBlock) {
        int radius = 5;
        List<Location> circleBlocks = new ArrayList<>(radius * radius * 4);

        int bx = centerBlock.getBlockX();
        int by = centerBlock.getBlockY();
        int bz = centerBlock.getBlockZ();

        for (int x = bx - radius; x <= bx + radius; x++) {
            int dx = bx - x;
            int dx2 = dx * dx;

            for (int z = bz - radius; z <= bz + radius; z++) {
                int dz = bz - z;
                int dz2 = dz * dz;
                int xzDistance = dx2 + dz2;

                int radiusSquared = radius * radius;
                if (xzDistance <= radiusSquared) {
                    for (int y = by - radius; y <= by + radius; y++) {
                        int dy = by - y;
                        int distance = xzDistance + (dy * dy);

                        int innerRadiusSquared = (radius - 1) * (radius - 1);
                        if (distance <= radiusSquared && distance >= innerRadiusSquared) {
                            circleBlocks.add(new Location(centerBlock.getWorld(), x, y, z));
                        }
                    }
                }
            }
        }

        return circleBlocks;
    }
}
