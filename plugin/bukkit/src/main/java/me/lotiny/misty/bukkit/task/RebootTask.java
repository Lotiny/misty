package me.lotiny.misty.bukkit.task;

import io.fairyproject.mc.scheduler.MCSchedulers;
import io.fairyproject.util.CC;
import lombok.Getter;
import me.lotiny.misty.api.task.AbstractScheduleTask;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.impl.MainConfig;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.TimeFormatUtils;
import me.lotiny.misty.bukkit.utils.Utilities;
import org.bukkit.Bukkit;

@Getter
public class RebootTask extends AbstractScheduleTask {

    @Override
    public Runnable tick() {
        return () -> {
            decrementSeconds();

            if (getSeconds() == 0) {
                cancel();
                return;
            }

            if (isImportanceSeconds(getSeconds())) {
                sendCountdownMessage();

                if (getSeconds() == 5) {
                    //noinspection deprecation
                    MCSchedulers.getGlobalScheduler().schedule(() -> Bukkit.getOnlinePlayers().forEach((player) -> player.kickPlayer(CC.RED + "The game has been ended!")));
                }
            }
        };
    }

    @Override
    public void onStart() {
        setSeconds(60);
        sendCountdownMessage();

        MainConfig config = Config.getMainConfig();
        config.getWorld().setLoaded(false);
        config.getWorld().setPlayed(true);
    }

    @Override
    public void onCancel() {
        MCSchedulers.getGlobalScheduler().schedule(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop"));
    }

    private void sendCountdownMessage() {
        Utilities.broadcast(Message.REBOOT_TIME.toString()
                .replace("<time>", TimeFormatUtils.formatTimeUnit(getSeconds())));
    }
}
