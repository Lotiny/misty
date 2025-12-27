package me.lotiny.misty.bukkit.provider.hotbar.listener;

import io.fairyproject.bootstrap.bukkit.BukkitPlugin;
import io.fairyproject.bukkit.listener.ListenerRegistry;
import io.fairyproject.container.InjectableComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@InjectableComponent
public class HotBarListenerRegistry implements ListenerRegistry {

    @Override
    public void register(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, BukkitPlugin.INSTANCE);
    }

    @Override
    public void unregister(Listener listener) {
        HandlerList.unregisterAll(listener);
    }
}
