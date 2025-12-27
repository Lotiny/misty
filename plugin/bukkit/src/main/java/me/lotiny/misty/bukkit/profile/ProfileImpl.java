package me.lotiny.misty.bukkit.profile;

import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.api.profile.stats.StatType;
import me.lotiny.misty.api.profile.stats.Stats;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class ProfileImpl implements Profile {

    private final UUID uniqueId;

    private String name;
    private Map<StatType, Stats> statsMap;

    public ProfileImpl(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Map<StatType, Stats> getStatsMap() {
        return this.statsMap;
    }

    @Override
    public void setStatsMap(Map<StatType, Stats> statsMap) {
        this.statsMap = statsMap;
    }

    @Override
    public Stats getStats(StatType statType) {
        return this.statsMap.get(statType);
    }
}
