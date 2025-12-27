package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import io.fairyproject.mc.scheduler.MCSchedulers;
import io.fairyproject.scheduler.ScheduledTask;
import io.fairyproject.util.CC;
import me.lotiny.misty.api.event.GracePeriodEndEvent;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class TrackerScenario extends Scenario {

    @Autowired
    private static GameManager gameManager;

    private ScheduledTask<?> task;

    @Override
    public String getName() {
        return "Tracker";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.COMPASS)
                .name("&b" + getName())
                .lore(
                        "&7When PVP Enabled all players will receive a Tracker Compass",
                        "&7that track closest player location."
                )
                .build();
    }

    @EventHandler
    public void handlePvpEnabled(GracePeriodEndEvent event) {
        List<UUID> uuids = gameManager.getRegistry().getAlivePlayers();
        uuids.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.getInventory().addItem(ItemStackUtils.of(XMaterial.COMPASS));
            }
        });

        task = MCSchedulers.getAsyncScheduler().scheduleAtFixedRate(() -> {
            uuids.forEach(uuid -> {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    ItemStack item = PlayerUtils.getItemInHand(player);
                    if (XMaterial.COMPASS.isSimilar(item)) {
                        Location location = player.getLocation();
                        Entity target = location.getWorld().getNearbyEntities(location, 1000, 100, 1000)
                                .stream()
                                .filter(entity -> entity instanceof Player)
                                .filter(entity -> UHCUtils.getTeam((Player) entity) != UHCUtils.getTeam(player))
                                .findFirst()
                                .orElse(null);

                        if (target == null) {
                            changeItemName(item, "&cFailed to find nearest player.");
                        } else {
                            changeItemName(item, "&b" + target.getName() + "&7: &f" + Math.round(location.distance(target.getLocation())) + " Block(s)");
                            player.setCompassTarget(target.getLocation());
                        }
                    }
                }
            });
        }, 20L, 20L);
    }

    @Override
    public void onDisable() {
        if (task != null) {
            task.cancel();
        }
    }

    @SuppressWarnings("deprecation")
    private void changeItemName(ItemStack stack, String name) {
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(CC.translate(name));
        stack.setItemMeta(meta);
    }
}
