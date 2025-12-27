package me.lotiny.misty.bukkit.utils;

import io.fairyproject.bootstrap.bukkit.BukkitPlugin;
import io.fairyproject.mc.MCServer;
import io.fairyproject.mc.version.MCVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VersionUtils {

    private final String PLUGIN_VERSION;

    static {
        if (isHigher(21, 0)) {
            PLUGIN_VERSION = BukkitPlugin.INSTANCE.getPluginMeta().getVersion();
        } else {
            //noinspection deprecation
            PLUGIN_VERSION = BukkitPlugin.INSTANCE.getDescription().getVersion();
        }
    }

    public boolean isLower(int minor, int patch) {
        return MCServer.current().getVersion().isLowerOrEqual(MCVersion.of(minor, patch));
    }

    public boolean isHigher(int minor, int patch) {
        return MCServer.current().getVersion().isHigherOrEqual(MCVersion.of(minor, patch));
    }

    public boolean is(int minor, int patch) {
        return MCServer.current().getVersion().isEqual(MCVersion.of(minor, patch));
    }

    public boolean isBetween(int lowerMinor, int lowerPatch, int higherMinor, int higherPatch) {
        return MCServer.current().getVersion().isBetweenOrEqual(MCVersion.of(lowerMinor, lowerPatch), MCVersion.of(higherMinor, higherPatch));
    }

    public String getPluginVersion() {
        return PLUGIN_VERSION;
    }
}
