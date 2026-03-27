package me.lotiny.misty.listener;

import io.fairyproject.bukkit.events.BukkitEventFilter;
import io.fairyproject.bukkit.events.BukkitEventNode;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.container.PreDestroy;
import io.fairyproject.event.EventListener;
import io.fairyproject.event.EventNode;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.game.registry.GameRegistry;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

@InjectableComponent
@RequiredArgsConstructor
public class WorldListener {

    private final GameManager gameManager;
    private final BukkitEventNode globalNode;

    private EventNode<Event> eventNode;

    @PostInitialize
    public void onPostInit() {
        this.eventNode = EventNode.type("world-listeners", BukkitEventFilter.ALL);

        EventListener<CreatureSpawnEvent> creatureSpawnListener = EventListener.builder(CreatureSpawnEvent.class)
                .filter(event -> isLobbyWorld(event.getEntity().getWorld()))
                .expireWhen(event -> gameManager.getRegistry().getState() == GameState.INGAME)
                .handler(event -> event.setCancelled(true))
                .build();

        eventNode.addListener(creatureSpawnListener);

        EventListener<LeavesDecayEvent> leavesDecayListener = EventListener.builder(LeavesDecayEvent.class)
                .filter(event -> isLobbyWorld(event.getBlock().getWorld()))
                .expireWhen(event -> gameManager.getRegistry().getState() == GameState.INGAME)
                .handler(event -> event.setCancelled(true))
                .build();

        eventNode.addListener(leavesDecayListener);

        eventNode.addListener(WeatherChangeEvent.class, event -> {
            if (event.toWeatherState()) {
                event.setCancelled(true);
            }
        });

        eventNode.addListener(ThunderChangeEvent.class, event -> {
            if (event.toThunderState()) {
                event.setCancelled(true);
            }
        });

        globalNode.addChild(eventNode);
    }

    @PreDestroy
    public void onPreDestroy() {
        globalNode.removeChild(eventNode);
    }

    private boolean isLobbyWorld(World world) {
        GameRegistry registry = gameManager.getRegistry();
        return !registry.getUhcWorld().equals(world.getName())
                && !registry.getNetherWorld().equals(world.getName());
    }
}
