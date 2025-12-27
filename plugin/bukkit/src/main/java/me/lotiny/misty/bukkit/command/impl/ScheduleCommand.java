package me.lotiny.misty.bukkit.command.impl;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.command.MessageType;
import io.fairyproject.command.annotation.Arg;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.util.FormatUtil;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.manager.PracticeManager;
import me.lotiny.misty.bukkit.task.StartTask;
import me.lotiny.misty.bukkit.utils.TimeFormatUtils;
import org.bukkit.Bukkit;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@InjectableComponent
@RequiredArgsConstructor
@Command(value = {"schedule", "autostart"}, permissionNode = "misty.command.schedule")
public class ScheduleCommand extends AbstractCommand {

    private final GameManager gameManager;
    private final PracticeManager practiceManager;

    @Command("#")
    public void onCommand(BukkitCommandContext context, @Arg("time") String time) {
        GameRegistry registry = gameManager.getRegistry();
        StartTask startTask = (StartTask) registry.getStartTask();

        if (time.equalsIgnoreCase("cancel")) {
            if (startTask == null) {
                context.sendMessage(MessageType.INFO, Message.SCHEDULE_NO_SCHEDULE.toString());
            } else {
                startTask.remove();
                registry.setStartTask(null);
                context.sendMessage(MessageType.INFO, Message.SCHEDULE_CANCEL.toString());
            }

            return;
        }

        boolean force = false;
        if (startTask != null) {
            if (startTask.isForce()) {
                context.sendMessage(MessageType.INFO, Message.SCHEDULE_ALREADY_SET.toString());
                return;
            }

            force = true;
        }

        if (time.startsWith("@")) {
            Pattern pattern = Pattern.compile("@(\\d+)(?::(\\d+))?");
            Matcher matcher = pattern.matcher(time);

            int hour = 0;
            int minute = 0;

            if (matcher.find()) {
                hour = Integer.parseInt(matcher.group(1));
                String minuteStr = matcher.group(2);
                if (minuteStr != null) {
                    minute = Integer.parseInt(minuteStr);
                }
            }

            TimeZone timeZone = TimeZone.getTimeZone(registry.getZoneId());
            Calendar now = Calendar.getInstance(timeZone);
            Calendar targetTime = Calendar.getInstance(timeZone);
            targetTime.set(Calendar.HOUR_OF_DAY, hour);
            targetTime.set(Calendar.MINUTE, minute);
            targetTime.set(Calendar.SECOND, 0);

            if (targetTime.before(now)) {
                targetTime.add(Calendar.DATE, 1);
            }

            long inputTime = targetTime.getTimeInMillis() - now.getTimeInMillis();

            setStartTimer(context, inputTime, registry, force);
            return;
        }

        long inputTime = TimeFormatUtils.handleParseTime(time);
        if (inputTime > -1L) {
            setStartTimer(context, inputTime, registry, force);
        }
    }

    private void setStartTimer(BukkitCommandContext context, long inputTime, GameRegistry registry, boolean force) {
        int seconds = Math.toIntExact(inputTime / 1000);

        if (seconds < 10) {
            context.sendMessage(MessageType.INFO, Message.SCHEDULE_MINIMUM.toString());
            return;
        }

        StartTask task;
        if (force) {
            task = (StartTask) registry.getStartTask();
            if (task != null) {
                task.remove();
            }
        }

        task = new StartTask(seconds, true);
        registry.setStartTask(task);
        task.run(true, 20L);

        context.sendMessage(MessageType.INFO, Message.SCHEDULE_SET.toString()
                .replace("<time>", FormatUtil.formatMillis(inputTime)));

        if (seconds <= (Config.getMainConfig().getWhitelistOffBefore() * 60)) {
            if (registry.isWhitelist()) {
                registry.setWhitelist(Bukkit.getConsoleSender(), false);
            }
            if (seconds <= 60) {
                if (practiceManager.isOpened()) {
                    practiceManager.setOpened(false, Bukkit.getConsoleSender());
                }
            }
        }
    }
}
