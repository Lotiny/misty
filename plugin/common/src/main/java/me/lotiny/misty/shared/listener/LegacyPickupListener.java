package me.lotiny.misty.shared.listener;

import me.lotiny.misty.shared.event.PlayerPickupItemEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LegacyPickupListener implements Listener {

    @EventHandler
    public void handlePickupItem(@SuppressWarnings("deprecation") org.bukkit.event.player.PlayerPickupItemEvent event) {
        PlayerPickupItemEvent pickupItemEvent = new PlayerPickupItemEvent(event.getPlayer(), event.getItem(), event.getRemaining(), event);
        Bukkit.getPluginManager().callEvent(pickupItemEvent);

        if (pickupItemEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }
}
