package me.lotiny.misty.bukkit.listener;

import io.fairyproject.Fairy;
import io.fairyproject.bukkit.listener.RegisterAsListener;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.util.FastRandom;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.bukkit.hook.PluginHookManager;
import me.lotiny.misty.bukkit.utils.LocationEx;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

@InjectableComponent
@RequiredArgsConstructor
@RegisterAsListener
public class WorldListener implements Listener {

    private final GameManager gameManager;
    private final PluginHookManager pluginHookManager;

    @EventHandler
    public void handleMobSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        World world = event.getLocation().getWorld();
        if (world == null) return;

        GameRegistry registry = gameManager.getRegistry();
        String worldName = world.getName();
        GameState state = registry.getState();

        if (state != GameState.INGAME ||
                (!worldName.equals(registry.getUhcWorld()) && !worldName.equals(registry.getNetherWorld()))) {
            event.setCancelled(true);
            return;
        }

        if (pluginHookManager.isLegacySupport()) {
            if (entity instanceof Rabbit) {
                event.setCancelled(true);
                world.spawnEntity(event.getLocation(), EntityType.COW);
            } else if (entity instanceof Guardian || entity instanceof Endermite) {
                event.setCancelled(true);
            }
            return;
        }

        FastRandom random = Fairy.random();
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

        if (entity instanceof Monster && reason == CreatureSpawnEvent.SpawnReason.NATURAL) {
            if (random.nextInt(100) > 40) {
                event.setCancelled(true);
            }
            return;
        }

        if (entity instanceof Cow && reason == CreatureSpawnEvent.SpawnReason.NATURAL && random.nextInt(100) < 50) {
            world.spawnEntity(event.getLocation(), EntityType.COW);
        }
    }

    @EventHandler
    public void handleWeatherChangeEvent(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleThunderChangeEvent(ThunderChangeEvent event) {
        if (event.toThunderState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleLeavesDecayEvent(LeavesDecayEvent event) {
        Block block = event.getBlock();
        World world = block.getWorld();
        if (world == LocationEx.LOBBY.getLocation().getWorld()
                || world == LocationEx.PRACTICE.getLocation().getWorld()) {
            event.setCancelled(true);
        }
    }
}
