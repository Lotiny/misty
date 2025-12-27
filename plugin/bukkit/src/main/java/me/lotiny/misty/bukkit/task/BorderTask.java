package me.lotiny.misty.bukkit.task;

import com.cryptomorin.xseries.XSound;
import io.fairyproject.container.Autowired;
import lombok.Getter;
import lombok.Setter;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.task.AbstractScheduleTask;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.manager.border.BorderManager;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.TimeFormatUtils;
import me.lotiny.misty.bukkit.utils.Utilities;

@Getter
@Setter
public class BorderTask extends AbstractScheduleTask {

    @Autowired
    private static GameManager gameManager;
    @Autowired
    private static BorderManager borderManager;

    private boolean forceShrink;
    private int size;

    @Override
    public Runnable tick() {
        return () -> {
            if (!shouldContinue()) {
                cancel();
                return;
            }

            if (!gameManager.getRegistry().isFirstShrunk()) {
                handleFirstShrink();
            } else {
                handleRegularShrink();
            }
        };
    }

    @Override
    public void onStart() {
        setSeconds(0);
        forceShrink = false;
    }

    private boolean shouldContinue() {
        return gameManager.getRegistry().isCanShrink() &&
                gameManager.getRegistry().getState() != GameState.ENDING;
    }

    private void handleFirstShrink() {
        incrementSeconds();

        int firstShrinkTime = gameManager.getGame().getSetting().getFirstShrink() * 60;

        if (isImportantSecondFirstShrink(firstShrinkTime)) {
            PlayerUtils.playSound(XSound.BLOCK_NOTE_BLOCK_HARP);
            createBorderMessageFirstShrink(firstShrinkTime);
        }

        if (getSeconds() == firstShrinkTime) {
            gameManager.getRegistry().setFirstShrunk(true);
            setSeconds(borderManager.getBorderInterval() * 60);
            if (forceShrink) {
                borderManager.handleStartSeconds(size);
                forceShrink = false;
            } else {
                borderManager.handleStartSeconds(borderManager.getNextBorder());
            }
            PlayerUtils.playSound(XSound.ENTITY_EXPERIENCE_ORB_PICKUP);
        }
    }

    private boolean isImportantSecondFirstShrink(int firstShrinkTime) {
        return getSeconds() == firstShrinkTime - 60 || getSeconds() == firstShrinkTime - 30 || getSeconds() == firstShrinkTime - 10 ||
                getSeconds() == firstShrinkTime - 5 || getSeconds() == firstShrinkTime - 4 || getSeconds() == firstShrinkTime - 3 ||
                getSeconds() == firstShrinkTime - 2 || getSeconds() == firstShrinkTime - 1;
    }

    private void createBorderMessageFirstShrink(int firstShrinkTime) {
        int shrinkIn = firstShrinkTime - getSeconds();
        Utilities.broadcast(Message.BORDER_SHRINKING_TIME.toString()
                .replace("<size>", forceShrink ? String.valueOf(size) : String.valueOf(borderManager.getNextBorder()))
                .replace("<time>", TimeFormatUtils.formatTimeUnit(shrinkIn)));
    }

    private void handleRegularShrink() {
        decrementSeconds();

        if (isImportantSecondRegularShrink(getSeconds())) {
            PlayerUtils.playSound(XSound.BLOCK_NOTE_BLOCK_HARP);
            createBorderMessageRegularShrink(getSeconds());
        }

        if (getSeconds() == 0) {
            setSeconds(borderManager.getBorderInterval() * 60);
            if (forceShrink) {
                borderManager.handleStartSeconds(size);
                forceShrink = false;
            } else {
                borderManager.handleStartSeconds(borderManager.getNextBorder());
            }
            PlayerUtils.playSound(XSound.ENTITY_EXPERIENCE_ORB_PICKUP);
        }
    }

    private boolean isImportantSecondRegularShrink(int seconds) {
        return seconds == 240 || seconds == 180 || seconds == 120 || seconds == 60 || seconds == 30 || seconds == 10 ||
                seconds == 5 || seconds == 4 || seconds == 3 || seconds == 2 || seconds == 1;
    }

    private void createBorderMessageRegularShrink(int seconds) {
        String sizeValue = forceShrink ? String.valueOf(size) : String.valueOf(borderManager.getNextBorder());
        String timeValue = TimeFormatUtils.formatTimeUnit(seconds);

        Utilities.broadcast(Message.BORDER_SHRINKING_TIME.toString()
                .replace("<size>", sizeValue)
                .replace("<time>", timeValue));
    }
}

