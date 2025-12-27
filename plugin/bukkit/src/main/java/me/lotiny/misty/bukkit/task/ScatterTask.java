package me.lotiny.misty.bukkit.task;

import io.fairyproject.container.Autowired;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.task.AbstractScheduleTask;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.TeamEx;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import me.lotiny.misty.bukkit.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ScatterTask extends AbstractScheduleTask {

    @Autowired
    private static GameManager gameManager;

    @Override
    public Runnable tick() {
        return () -> {
            GameRegistry registry = gameManager.getRegistry();
            if (registry.getPlayersToScatter().isEmpty()) {
                cancel();
                return;
            }

            UUID uuid = registry.getPlayersToScatter().removeFirst();
            scatter(uuid, registry);
        };
    }

    @Override
    public void onCancel() {
        Utilities.broadcast(Message.SCATTER_FINISHED.toString());

        LastCountdownTask task = new LastCountdownTask();
        gameManager.getRegistry().setLastCountdownTask(task);
        task.run(false, 20L);
    }

    private void scatter(UUID uuid, GameRegistry registry) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;

        Location location = UHCUtils.getTeam(player).getStorage().getOrNull(TeamEx.SCATTER_LOCATION);
        if (location == null) {
            World world = Bukkit.getWorld(registry.getUhcWorld());
            if (world == null) return;

            location = gameManager.findSafeScatterLocation(world, gameManager.getGame().getSetting().getBorderSize());
        }

        player.teleport(location);
        registry.getPlayersScattered().add(uuid);
    }
}