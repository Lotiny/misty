package me.lotiny.misty.shared.listener;

import me.lotiny.misty.shared.event.PlayerPickupItemEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class ModernPickupListener implements Listener {

    @EventHandler
    public void handlePickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        PlayerPickupItemEvent pickupItemEvent = new PlayerPickupItemEvent(player, event.getItem(), event.getRemaining(), event);
        Bukkit.getPluginManager().callEvent(pickupItemEvent);

        if (pickupItemEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }
}
