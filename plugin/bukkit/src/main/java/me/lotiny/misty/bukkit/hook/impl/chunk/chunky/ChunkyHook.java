package me.lotiny.misty.bukkit.hook.impl.chunk.chunky;

import io.fairyproject.container.Containers;
import io.fairyproject.log.Log;
import io.fairyproject.mc.scheduler.MCSchedulers;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.impl.MainConfig;
import me.lotiny.misty.bukkit.hook.PluginHook;
import me.lotiny.misty.bukkit.hook.impl.chunk.ChunkLoader;
import me.lotiny.misty.bukkit.manager.WorldManager;
import me.lotiny.misty.bukkit.utils.Utilities;
import org.bukkit.Bukkit;
import org.popcraft.chunky.api.ChunkyAPI;

public class ChunkyHook implements PluginHook, ChunkLoader {

    private final GameManager gameManager;
    private final WorldManager worldManager;

    private int size;
    private String world;
    private float progress;
    private long chunks;
    private boolean completed = true;

    private ChunkyAPI chunky;

    public ChunkyHook() {
        this.gameManager = Containers.get(GameManager.class);
        this.worldManager = Containers.get(WorldManager.class);
    }

    @Override
    public void register() {
        chunky = Bukkit.getServer().getServicesManager().load(ChunkyAPI.class);
        if (chunky == null) return;

        chunky.onGenerationComplete(event -> {
            String world = event.world();
            String nether = gameManager.getRegistry().getNetherWorld();
            Utilities.broadcast("&aFinished loading the world &2" + world + "&a!");
            if (world.equals(gameManager.getRegistry().getUhcWorld())) {
                MCSchedulers.getGlobalScheduler().schedule(() -> fillWorld(nether, size / worldManager.getNetherScale()));
            } else if (world.equals(nether)) {
                this.completed = true;

                MainConfig config = Config.getMainConfig();
                config.getWorld().setLoaded(true);
                config.getWorld().setPlayed(false);
                config.save();

                Utilities.broadcast("&cServer rebooting in 5 seconds...");
                Utilities.stop(100L);
            }
        });

        chunky.onGenerationProgress(event -> {
            this.world = event.world();
            this.progress = Math.round(event.progress() * 10f) / 10f;
            this.chunks = event.chunks();
        });
        Log.info("Hooked 'Chunky' for Chunk Loader support.");
    }

    @Override
    public void fillWorld(String world, int size) {
        this.setSize(world, size);
        chunky.startTask(world, "square", 0, 0, size, size, "concentric");

        this.completed = false;
        if (world.equals(gameManager.getRegistry().getUhcWorld())) {
            this.size = size;
        }
    }

    @Override
    public void setSize(String world, int size) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "chunky world " + world);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "chunky shape square");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "chunky radius " + size);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "chunky border add");
    }

    @Override
    public boolean isCompleted() {
        return this.completed;
    }

    @Override
    public String getWorld() {
        return this.world;
    }

    @Override
    public float getProgress() {
        return this.progress;
    }

    @Override
    public long getChunks() {
        return this.chunks;
    }
}
