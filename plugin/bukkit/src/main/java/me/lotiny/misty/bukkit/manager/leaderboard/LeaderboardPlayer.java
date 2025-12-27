package me.lotiny.misty.bukkit.manager.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LeaderboardPlayer {

    private int place;
    private String name;
    private String value;
}
