package me.lotiny.misty.shared.listener;

import me.lotiny.misty.shared.event.PlayerPickupItemEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LegacyListener implements Listener {

    @EventHandler
    public void handlePickupItem(@SuppressWarnings("deprecation") org.bukkit.event.player.PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItem();
        int remaining = event.getRemaining();

        PlayerPickupItemEvent pickupItemEvent = new PlayerPickupItemEvent(player, item, remaining, event);
        Bukkit.getPluginManager().callEvent(pickupItemEvent);

        if (pickupItemEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }
}
