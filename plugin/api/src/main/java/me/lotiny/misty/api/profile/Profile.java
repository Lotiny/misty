package me.lotiny.misty.api.profile;

import me.lotiny.misty.api.profile.stats.StatType;
import me.lotiny.misty.api.profile.stats.Stats;

import java.util.Map;
import java.util.UUID;

public interface Profile {

    UUID getUniqueId();

    String getName();

    void setName(String name);

    Map<StatType, Stats> getStatsMap();

    void setStatsMap(Map<StatType, Stats> statsMap);

    Stats getStats(StatType statType);
}
