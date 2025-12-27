package me.lotiny.misty.bukkit.game.listeners;

import io.fairyproject.bukkit.player.movement.MovementListener;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.util.CC;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.bukkit.Permission;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@InjectableComponent
@RequiredArgsConstructor
public class PlayerMoveListener implements MovementListener {

    private final GameManager gameManager;

    @Override
    public void handleUpdateLocation(Player player, Location from, Location to) {
        GameRegistry registry = gameManager.getRegistry();

        if (registry.getState() == GameState.INGAME) {
            if (!UHCUtils.isAlive(player.getUniqueId()) && !player.hasPermission(Permission.SPECTATE_BYPASS)) {
                if (Math.abs(to.getBlockX()) > 100 || Math.abs(to.getBlockZ()) > 100) {
                    player.teleport(from);
                    player.sendMessage(CC.translate("&cYou may only spectate within a 100x100 blocks area!"));
                }
            }
        }
    }
}