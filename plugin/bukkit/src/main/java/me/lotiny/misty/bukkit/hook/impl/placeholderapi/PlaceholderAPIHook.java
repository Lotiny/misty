package me.lotiny.misty.bukkit.hook.impl.placeholderapi;

import io.fairyproject.log.Log;
import io.fairyproject.mc.MCPlayer;
import me.clip.placeholderapi.PlaceholderAPI;
import me.lotiny.misty.bukkit.hook.PluginHook;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook implements PluginHook, PlaceholderAPISupport {

    @Override
    public void register() {
        new MistyPlaceholderAPIExpansion().register();

        Log.info("Hooked 'PlaceholderAPI' for Placeholder support.");
    }

    @Override
    public String replace(MCPlayer mcPlayer, String message) {
        return PlaceholderAPI.setPlaceholders(mcPlayer.as(Player.class), message);
    }
}
