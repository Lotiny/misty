package me.lotiny.misty.api.task;

import io.fairyproject.mc.scheduler.MCSchedulers;
import io.fairyproject.scheduler.ScheduledTask;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public abstract class AbstractScheduleTask {

    protected ScheduledTask<?> task;
    protected List<Integer> importanceSeconds = List.of(1800, 1200, 900, 600, 300, 240, 180, 120, 60, 30, 20, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1);

    @Getter
    @Setter
    private int seconds;

    public void run(boolean async, long interval) {
        onStart();
        task = async ? MCSchedulers.getAsyncScheduler().scheduleAtFixedRate(tick(), 20L, interval) : MCSchedulers.getGlobalScheduler().scheduleAtFixedRate(tick(), 20L, interval);
    }

    public abstract Runnable tick();

    public void cancel() {
        task.cancel();
        onCancel();
    }

    public void remove() {
        task.cancel();
    }

    public void onStart() {

    }

    public void onCancel() {

    }

    public boolean isImportanceSeconds(int seconds) {
        return importanceSeconds.contains(seconds);
    }

    public void decrementSeconds() {
        this.seconds--;
    }

    public void incrementSeconds() {
        this.seconds++;
    }
}
