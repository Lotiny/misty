package me.lotiny.misty.bukkit.hook.impl.placeholderapi;

import io.fairyproject.mc.MCPlayer;

public interface PlaceholderAPISupport {

    String replace(MCPlayer mcPlayer, String message);
}
