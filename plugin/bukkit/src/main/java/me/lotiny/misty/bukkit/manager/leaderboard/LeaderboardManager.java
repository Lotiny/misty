package me.lotiny.misty.bukkit.manager.leaderboard;

import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.container.PreDestroy;
import io.fairyproject.log.Log;
import io.fairyproject.mc.scheduler.MCSchedulers;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.profile.stats.StatType;
import me.lotiny.misty.bukkit.storage.StorageRegistry;
import me.lotiny.misty.bukkit.utils.Utilities;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@InjectableComponent
@RequiredArgsConstructor
public class LeaderboardManager {

    private final StorageRegistry storageRegistry;

    @Getter
    private Map<StatType, Leaderboard> leaderboardMap;

    @PostInitialize
    public void onPostInit() {
        this.leaderboardMap = new HashMap<>();

        for (StatType statType : StatType.values()) {
            this.leaderboardMap.put(statType, new Leaderboard(statType, Utilities.getFormattedName(statType.name()), statType.getData()));
        }

        MCSchedulers.getAsyncScheduler().scheduleAtFixedRate(() -> {
            this.savePlayerData();
            this.updateLeaderBoard();
            storageRegistry.getLeaderboardHologramStorage().getAll().forEach(LeaderboardHologram::spawn);
        }, Duration.ofSeconds(5), Duration.ofMinutes(10));
    }

    @PreDestroy
    public void onPreDestroy() {
        storageRegistry.getLeaderboardHologramStorage().getAll().forEach(LeaderboardHologram::remove);
    }

    public void savePlayerData() {
        long startTime = System.currentTimeMillis();

        storageRegistry.getProfileStorage().saveAll();
        long endTime = System.currentTimeMillis();
        Log.info("[Misty] All player's data saved! Took " + (endTime - startTime) + "ms to save.");
    }

    public void updateLeaderBoard() {
        long startTime = System.currentTimeMillis();

        leaderboardMap.forEach((statType, leaderBoard) -> {
            leaderBoard.getPlayers().clear();

            storageRegistry.getProfileStorage().getTops(10, statType.getData()).forEach((place, profile) -> {
                LeaderboardPlayer leaderBoardPlayer = new LeaderboardPlayer(
                        place,
                        profile.getName(),
                        String.valueOf(profile.getStats(statType).getAmount())
                );

                leaderBoard.getPlayers().put(place, leaderBoardPlayer);
            });
        });

        long endTime = System.currentTimeMillis();
        Log.info("[Misty] Leaderboards updated! Took " + (endTime - startTime) + "ms to update.");
    }
}
