package me.lotiny.misty.listener;

import io.fairyproject.bukkit.events.BukkitEventFilter;
import io.fairyproject.bukkit.events.BukkitEventNode;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.container.PreDestroy;
import io.fairyproject.event.EventNode;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.utils.Message;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

@InjectableComponent
@RequiredArgsConstructor
public class WhitelistListener {

    private final GameManager gameManager;
    private final BukkitEventNode globalNode;

    private EventNode<Event> eventNode;

    @PostInitialize
    public void onPostInit() {
        this.eventNode = EventNode.type("whitelist-listeners", BukkitEventFilter.ALL);

        eventNode.addListener(AsyncPlayerPreLoginEvent.class, event -> {
            GameRegistry registry = gameManager.getRegistry();
            GameState state = registry.getState();

            String message = null;
            if (state == GameState.SCATTERING) {
                message = Message.LOGIN_DISALLOW_SCATTER;
            } else if (state == GameState.ENDING) {
                message = Message.LOGIN_DISALLOW_END;
            } else if (registry.isWhitelist() && !registry.getWhitelistPlayers().contains(event.getName())) {
                message = Message.LOGIN_DISALLOW_WHITELIST;
            }

            if (message != null) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, message);
                return;
            }

            event.allow();
        });

        globalNode.addChild(eventNode);
    }

    @PreDestroy
    public void onPreDestroy() {
        globalNode.removeChild(eventNode);
    }
}
