package me.lotiny.misty.bukkit.hook;

import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.log.Log;
import io.fairyproject.mc.MCPlayer;
import io.fairyproject.util.CC;
import io.github.retrooper.packetevents.util.viaversion.ViaVersionUtil;
import lombok.Getter;
import me.lotiny.misty.bukkit.hook.impl.apollo.ApolloHook;
import me.lotiny.misty.bukkit.hook.impl.chunk.ChunkLoader;
import me.lotiny.misty.bukkit.hook.impl.chunk.chunky.ChunkyHook;
import me.lotiny.misty.bukkit.hook.impl.chunk.worldborder.WorldBorderHook;
import me.lotiny.misty.bukkit.hook.impl.placeholderapi.PlaceholderAPIHook;
import me.lotiny.misty.bukkit.hook.impl.placeholderapi.PlaceholderAPISupport;
import me.lotiny.misty.bukkit.hook.impl.svc.SVCHook;
import me.lotiny.misty.bukkit.hook.impl.viaversion.ViaVersionHook;
import me.lotiny.misty.bukkit.utils.Utilities;
import me.lotiny.misty.bukkit.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
@InjectableComponent
public class PluginHookManager {

    private ApolloHook apolloHook;
    private PlaceholderAPISupport placeholderApi;
    private ChunkLoader chunkLoader;

    private boolean legacySupport;

    @PostInitialize
    public void onPostInit() {
        if (this.hasPlugin("Apollo-Bukkit")) {
            ApolloHook hook = new ApolloHook();
            hook.register();
            apolloHook = hook;
        }

        if (this.hasPlugin("PlaceholderAPI")) {
            PlaceholderAPIHook hook = new PlaceholderAPIHook();
            hook.register();
            placeholderApi = hook;
        }

        if (VersionUtils.is(8, 8)) {
            if (this.hasPlugin("ViaVersion") && this.hasPlugin("ViaRewind")) {
                ViaVersionHook hook = new ViaVersionHook();
                hook.register();
                legacySupport = ViaVersionUtil.isAvailable();
            }

            if (this.hasPlugin("WorldBorder")) {
                WorldBorderHook hook = new WorldBorderHook();
                hook.register();
                chunkLoader = hook;
            }
        }

        if (VersionUtils.isHigher(16, 5)) {
            if (this.hasPlugin("voicechat")) {
                SVCHook hook = new SVCHook();
                hook.register();
            }
        }

        if (VersionUtils.isHigher(16, 5)) {
            if (this.hasPlugin("Chunky") && this.hasPlugin("ChunkyBorder")) {
                ChunkyHook hook = new ChunkyHook();
                hook.register();
                chunkLoader = hook;
            }
        }

        if (chunkLoader == null) {
            Log.error("Chunk Loader plugin not found! Please install 'WorldBorder' for 1.8.8 or 'Chunky' + 'ChunkyBorder' for 1.21.4!");
            Utilities.disable();
        }
    }

    public String replace(Player player, String message) {
        if (placeholderApi == null) {
            return CC.translate(message);
        } else {
            return CC.translate(placeholderApi.replace(MCPlayer.from(player), message));
        }
    }

    private boolean hasPlugin(String plugin) {
        return Bukkit.getPluginManager().getPlugin(plugin) != null && Bukkit.getPluginManager().isPluginEnabled(plugin);
    }
}
