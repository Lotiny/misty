package me.lotiny.misty.bukkit.utils;

import io.fairyproject.bootstrap.bukkit.BukkitPlugin;
import io.fairyproject.log.Log;
import io.fairyproject.mc.MCServer;
import lombok.experimental.UtilityClass;
import me.lotiny.misty.nms.v1_12_2.v1_12_2;
import me.lotiny.misty.nms.v1_16_5.v1_16_5;
import me.lotiny.misty.nms.v1_21.v1_21;
import me.lotiny.misty.nms.v1_21_11.v1_21_11;
import me.lotiny.misty.nms.v1_21_4.v1_21_4;
import me.lotiny.misty.nms.v1_8_8.v1_8_8;
import me.lotiny.misty.shared.ReflectionManager;
import me.lotiny.misty.shared.listener.LegacyPickupListener;
import me.lotiny.misty.shared.listener.ModernPickupListener;
import org.bukkit.Bukkit;

@UtilityClass
public class ReflectionUtils {

    private ReflectionManager instance;

    static {
        if (VersionUtils.is(8, 8)) {
            instance = new v1_8_8();
        } else if (VersionUtils.is(12, 2)) {
            instance = new v1_12_2();
        } else if (VersionUtils.is(16, 5)) {
            instance = new v1_16_5();
        } else if (VersionUtils.isBetween(21, 0, 21, 3)) {
            instance = new v1_21();
        } else if (VersionUtils.isBetween(21, 4, 21, 10)) {
            instance = new v1_21_4();
        } else if (VersionUtils.isHigher(21, 11)) {
            instance = new v1_21_11();
        } else {
            Log.error("Misty doesn't support this server version!");
            Bukkit.getPluginManager().disablePlugin(BukkitPlugin.INSTANCE);
        }

        registerEvent();
        Log.info("Loaded " + instance.getClass().getSimpleName() + " for server version " + MCServer.current().getVersion().getFormatted());
    }

    private void registerEvent() {
        if (VersionUtils.isHigher(12, 0)) {
            Bukkit.getPluginManager().registerEvents(new ModernPickupListener(), BukkitPlugin.INSTANCE);
        } else {
            Bukkit.getPluginManager().registerEvents(new LegacyPickupListener(), BukkitPlugin.INSTANCE);
        }
    }

    public ReflectionManager get() {
        return instance;
    }
}
