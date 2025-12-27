package me.lotiny.misty.bukkit.manager;

import io.fairyproject.container.DependsOn;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.log.Log;
import io.fairyproject.mc.scheduler.MCSchedulers;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.ConfigManager;
import me.lotiny.misty.bukkit.config.impl.MainConfig;
import me.lotiny.misty.bukkit.hook.PluginHookManager;
import me.lotiny.misty.bukkit.utils.LocationEx;
import me.lotiny.misty.bukkit.utils.ReflectionUtils;
import me.lotiny.misty.bukkit.utils.Utilities;
import org.bukkit.*;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Villager;

import java.io.File;

@DependsOn(ConfigManager.class)
@InjectableComponent
@RequiredArgsConstructor
public class WorldManager {

    private final GameManager gameManager;
    private final PluginHookManager pluginHookManager;

    @Getter
    private int netherScale;

    @PostInitialize
    public void onPostInit() {
        netherScale = Config.getMainConfig().getNetherScale();
        setupWorlds();
    }

    public void setupWorlds() {
        GameRegistry registry = gameManager.getRegistry();
        MainConfig config = Config.getMainConfig();

        if (config.getWorld().isPlayed()) {
            deleteWorld(registry.getUhcWorld());
            deleteWorld(registry.getNetherWorld());
        }

        createWorld(registry.getUhcWorld(), World.Environment.NORMAL, WorldType.NORMAL);
        createWorld(registry.getNetherWorld(), World.Environment.NETHER, WorldType.NORMAL);

        if (!config.getWorld().isLoaded()) {
            int borderSize = gameManager.getGame().getSetting().getBorderSize();
            int configSize = config.getPreGenerateWorldSize();
            loadWorld(registry.getUhcWorld(), configSize == -1 ? borderSize : configSize);
        }

        World world = LocationEx.LOBBY.getLocation().getWorld();
        if (world != null) {
            clearEntities(world);
            world.setTime(1000);
            ReflectionUtils.get().setGameRule(world, "doDaylightCycle", false);
        }
    }

    public void createWorld(String worldName, World.Environment environment, WorldType worldType) {
        Utilities.broadcast("&fStarted creating the world &b" + worldName + "&f...");
        World world = Bukkit.createWorld(new WorldCreator(worldName).environment(environment).type(worldType));
        if (world != null) {
            clearEntities(world);

            world.setDifficulty(Difficulty.EASY);
            world.setTime(0);
            world.setThundering(false);
            ReflectionUtils.get().setGameRule(world, "naturalRegeneration", false);
            ReflectionUtils.get().setGameRule(world, "doDaylightCycle", false);
        }

        Utilities.broadcast("&fFinished creating the world &b" + worldName + "&f!");
    }

    public void unloadUnusedWorld() {
        unloadWorld(LocationEx.LOBBY.getLocation().getWorld());
        if (!gameManager.getGame().getSetting().isNether()) {
            unloadWorld(Bukkit.getWorld(gameManager.getRegistry().getNetherWorld()));
        }
    }

    public void loadWorld(String worldName, int worldRadius) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) return;

        clearEntities(world);

        pluginHookManager.getChunkLoader().fillWorld(worldName, worldRadius);
        Utilities.broadcast("&fStarted loading the world &b" + worldName + "&f...");
    }

    public void unloadWorld(World world) {
        if (world != null) {
            Bukkit.unloadWorld(world, false);
        }
    }

    public void deleteWorld(String worldName) {
        Utilities.broadcast("&fStarted deleting the world &b" + worldName + "&f...");
        unloadWorld(Bukkit.getWorld(worldName));
        deleteFiles(new File(Bukkit.getWorldContainer(), worldName));
        Utilities.broadcast("&fFinished deleting the world &b" + worldName + "&f!");
    }

    public void clearEntities(World world) {
        MCSchedulers.getGlobalScheduler().schedule(() ->
                world.getEntitiesByClasses(Monster.class, Animals.class, Villager.class)
                        .forEach(Entity::remove)
        );
    }

    private void deleteFiles(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFiles(file);
                    } else {
                        if (!file.delete()) {
                            Log.error("Failed to delete file: " + file.getAbsolutePath());
                        }
                    }
                }
            }
        }
        if (!path.delete()) {
            Log.error("Failed to delete directory: " + path.getAbsolutePath());
        }
    }

}