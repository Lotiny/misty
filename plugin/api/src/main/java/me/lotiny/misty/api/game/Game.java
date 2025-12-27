package me.lotiny.misty.api.game;

import io.fairyproject.bootstrap.bukkit.BukkitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.List;

public interface Game {

    List<Listener> listener();

    GameSetting getSetting();

    void start();

    void stop();

    default void registerListeners() {
        listener().forEach(listener -> {
            Bukkit.getPluginManager().registerEvents(listener, BukkitPlugin.INSTANCE);
        });
    }

    default void unregisterListeners() {
        listener().forEach(HandlerList::unregisterAll);
    }
}
