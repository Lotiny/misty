package me.lotiny.misty.bukkit.task;

import io.fairyproject.container.Autowired;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.task.AbstractScheduleTask;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.Utilities;

public class AnnounceTask extends AbstractScheduleTask {

    @Autowired
    private static GameManager gameManager;

    private int interval;

    @Override
    public Runnable tick() {
        return () -> {
            decrementSeconds();

            if (gameManager.getRegistry().getPlayers().isEmpty()) return;

            if (getSeconds() == 0) {
                setSeconds(interval);

                int required = Config.getMainConfig().getAutoStart().getMinPlayers() - gameManager.getRegistry().getPlayers().size();
                Utilities.broadcast(Message.AUTOSTART_ANNOUNCE_MESSAGE.toString()
                        .replace("<required>", String.valueOf(required)));
            }

            AbstractScheduleTask task = gameManager.getRegistry().getStartTask();
            if (task != null) {
                cancel();
            }
        };
    }

    @Override
    public void onStart() {
        interval = Config.getMainConfig().getAutoStart().getAnnounce().getInterval();
        setSeconds(interval);
    }
}
