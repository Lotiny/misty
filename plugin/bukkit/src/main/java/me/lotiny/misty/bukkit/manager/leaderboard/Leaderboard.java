package me.lotiny.misty.bukkit.manager.leaderboard;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.profile.stats.StatType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@RequiredArgsConstructor
public class Leaderboard {

    private final Map<Integer, LeaderboardPlayer> players = new ConcurrentHashMap<>();

    private final StatType statType;
    private final String displayName;
    private final String data;
}
