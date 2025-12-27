package me.lotiny.misty.bukkit.task;

import io.fairyproject.container.Autowired;
import io.fairyproject.mc.scheduler.MCSchedulers;
import lombok.Getter;
import me.lotiny.misty.api.event.UHCMinuteEvent;
import me.lotiny.misty.api.game.ConfigType;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.task.AbstractScheduleTask;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.TimeFormatUtils;
import me.lotiny.misty.bukkit.utils.Utilities;
import org.bukkit.Bukkit;

@Getter
public class GameTask extends AbstractScheduleTask {

    @Autowired
    private static GameManager gameManager;

    @Override
    public Runnable tick() {
        return () -> {
            GameRegistry registry = gameManager.getRegistry();
            if (registry.getState() == GameState.ENDING) {
                cancel();
                return;
            }

            incrementSeconds();

            if (getSeconds() == 30) {
                registry.setDamage(true);
            }

            int finalHealTime = gameManager.getGame().getSetting().getFinalHeal() * 60;
            sendCountdown(finalHealTime, ConfigType.FINAL_HEAL);

            if (getSeconds() == finalHealTime) {
                if (!registry.isFinalHealHappened()) {
                    gameManager.executeFinalHeal();
                }
            }

            int graceTime = gameManager.getGame().getSetting().getGracePeriod() * 60;
            sendCountdown(graceTime, ConfigType.GRACE_PERIOD);

            if (getSeconds() == graceTime) {
                if (!registry.isPvpEnabled()) {
                    gameManager.endGracePeriod();
                }
            }

            if (getSeconds() % 60 == 0) {
                MCSchedulers.getGlobalScheduler().schedule(() -> {
                    Bukkit.getPluginManager().callEvent(new UHCMinuteEvent(getSeconds() / 60));
                }, 1L);
            }
        };
    }

    private void sendCountdown(int targetSeconds, ConfigType configType) {
        int remaining = targetSeconds - getSeconds();

        if (remaining == 600 || remaining == 300 || remaining == 60 ||
                remaining == 30 || remaining == 10 ||
                (remaining <= 5 && remaining > 0)) {
            if (configType == ConfigType.GRACE_PERIOD) {
                Utilities.broadcast(Message.GRACE_PERIOD_TIME.toString()
                        .replace("<time>", TimeFormatUtils.formatTimeUnit(remaining)));
            } else {
                Utilities.broadcast(Message.FINAL_HEAL_TIME.toString()
                        .replace("<time>", TimeFormatUtils.formatTimeUnit(remaining)));
            }
        }
    }

    @Override
    public void onStart() {
        setSeconds(0);
        Utilities.broadcast(Message.GAME_STATED.toString());
    }
}

