package me.lotiny.misty.bukkit.task;

import com.cryptomorin.xseries.XSound;
import io.fairyproject.container.Autowired;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.task.AbstractScheduleTask;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LastCountdownTask extends AbstractScheduleTask {

    @Autowired
    private static GameManager gameManager;

    @Override
    public Runnable tick() {
        return () -> {
            decrementSeconds();

            Bukkit.getOnlinePlayers().forEach(player -> {
                Team team = UHCUtils.getTeam(player);
                Location location = player.getLocation();
                Location scatterLocation = team.getStorage().getOrThrow(TeamEx.SCATTER_LOCATION);

                double dx = Math.abs(location.getX() - scatterLocation.getX());
                double dz = Math.abs(location.getZ() - scatterLocation.getZ());

                if (dx > 5 || dz > 5) {
                    player.teleport(scatterLocation);
                }
            });

            if (getSeconds() == 0) {
                cancel();
                return;
            }

            if (isImportanceSeconds(getSeconds())) {
                PlayerUtils.playSound(XSound.ENTITY_EXPERIENCE_ORB_PICKUP);
                Utilities.broadcast(Message.START_TIME.toString()
                        .replace("<time>", TimeFormatUtils.formatTimeUnit(getSeconds())));
            }
        };
    }

    @Override
    public void onStart() {
        setSeconds(Config.getMainConfig().getStabilizeSeconds());
    }

    @Override
    public void onCancel() {
        gameManager.getGame().start();
    }
}
