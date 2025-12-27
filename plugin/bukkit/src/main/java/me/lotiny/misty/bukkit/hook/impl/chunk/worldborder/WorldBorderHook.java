package me.lotiny.misty.bukkit.hook.impl.chunk.worldborder;

import com.wimbli.WorldBorder.Events.WorldBorderFillFinishedEvent;
import com.wimbli.WorldBorder.Events.WorldBorderFillStartEvent;
import com.wimbli.WorldBorder.WorldFillTask;
import io.fairyproject.bootstrap.bukkit.BukkitPlugin;
import io.fairyproject.container.Containers;
import io.fairyproject.log.Log;
import io.fairyproject.mc.scheduler.MCSchedulers;
import io.fairyproject.scheduler.ScheduledTask;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.impl.MainConfig;
import me.lotiny.misty.bukkit.hook.PluginHook;
import me.lotiny.misty.bukkit.hook.impl.chunk.ChunkLoader;
import me.lotiny.misty.bukkit.manager.WorldManager;
import me.lotiny.misty.bukkit.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class WorldBorderHook implements PluginHook, ChunkLoader, Listener {

    private final GameManager gameManager;
    private final WorldManager worldManager;

    private ScheduledTask<?> task;

    private int size;
    private String world;
    private float progress;
    private long chunks;
    private boolean completed = true;

    public WorldBorderHook() {
        this.gameManager = Containers.get(GameManager.class);
        this.worldManager = Containers.get(WorldManager.class);
    }

    @Override
    public void register() {
        Bukkit.getPluginManager().registerEvents(this, BukkitPlugin.INSTANCE);
        Log.info("Hooked 'WorldBorder' for Chunk Loader support.");
    }

    @Override
    public void fillWorld(String world, int size) {
        this.setSize(world, size);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb " + world + " fill 1000 96 false");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb fill confirm");

        this.completed = false;
        if (world.equals(gameManager.getRegistry().getUhcWorld())) {
            this.size = size;
        }
    }

    @Override
    public void setSize(String world, int size) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb shape square");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb " + world + " set " + size + " " + size + " 0 0");
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

    @EventHandler
    public void handleWorldBorderFillFinished(WorldBorderFillFinishedEvent event) {
        task.cancel();

        String world = event.getWorld().getName();
        String nether = gameManager.getRegistry().getNetherWorld();
        Utilities.broadcast("&aFinished loading the world &2" + world + "&a!");
        if (world.equals(gameManager.getRegistry().getUhcWorld())) {
            MCSchedulers.getGlobalScheduler().schedule(() -> {
                int netherSize = size / worldManager.getNetherScale();
                setSize(nether, netherSize);
                fillWorld(nether, netherSize);
            }, 20L);
        } else if (world.equals(nether)) {
            this.completed = true;

            MainConfig config = Config.getMainConfig();
            config.getWorld().setLoaded(true);
            config.getWorld().setPlayed(false);
            config.save();

            Utilities.broadcast("&cServer rebooting in 5 seconds...");
            Utilities.stop(100L);
        }
    }

    @EventHandler
    public void handleWorldBorderFillStart(WorldBorderFillStartEvent event) {
        task = MCSchedulers.getAsyncScheduler().scheduleAtFixedRate(() -> {
            WorldFillTask task = event.getFillTask();
            this.world = task.refWorld();
            this.progress = Math.round(task.getPercentageCompleted() * 10f) / 10f;
            this.chunks = task.getChunksCompleted();
        }, 0L, 2L);
    }
}
