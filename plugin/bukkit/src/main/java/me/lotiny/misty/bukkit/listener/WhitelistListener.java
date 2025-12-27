package me.lotiny.misty.bukkit.listener;

import io.fairyproject.bukkit.listener.RegisterAsListener;
import io.fairyproject.container.InjectableComponent;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.bukkit.enums.Message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

@InjectableComponent
@RequiredArgsConstructor
@RegisterAsListener
public class WhitelistListener implements Listener {

    private final GameManager gameManager;

    @EventHandler
    public void handlePlayerLoginEvent(AsyncPlayerPreLoginEvent event) {
        GameRegistry registry = gameManager.getRegistry();
        GameState state = registry.getState();

        String message = null;
        if (state == GameState.SCATTERING) {
            message = Message.LOGIN_DISALLOW_SCATTER.toString();
        } else if (state == GameState.ENDING) {
            message = Message.LOGIN_DISALLOW_END.toString();
        } else if (registry.isWhitelist() && !registry.getWhitelistPlayers().contains(event.getName())) {
            message = Message.LOGIN_DISALLOW_WHITELIST.toString();
        }

        if (message != null) {
            //noinspection deprecation
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, message);
            return;
        }

        event.allow();
    }
}
