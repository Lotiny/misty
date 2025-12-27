package me.lotiny.misty.bukkit.hook.rank;

import io.fairyproject.bootstrap.bukkit.BukkitPlugin;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.log.Log;
import lombok.Getter;
import me.lotiny.misty.bukkit.hook.rank.impl.AquaCore;
import me.lotiny.misty.bukkit.hook.rank.impl.Default;
import me.lotiny.misty.bukkit.hook.rank.impl.LuckPerms;
import org.bukkit.plugin.PluginManager;

@Getter
@InjectableComponent
public class RankManager {

    private IRank rank;

    @PostInitialize
    public void onPostInit() {
        init();
    }

    public void init() {
        PluginManager pluginManager = BukkitPlugin.INSTANCE.getServer().getPluginManager();
        if (pluginManager.getPlugin("AquaCore") != null) {
            rank = new AquaCore();
        } else if (pluginManager.getPlugin("LuckPerms") != null) {
            rank = new LuckPerms();
        } else {
            rank = new Default();
        }

        if (!rank.getRankSystem().equals("Default")) {
            Log.info("Hooked '" + rank.getClass().getSimpleName() + " API' for Core support.");
        }
    }
}


