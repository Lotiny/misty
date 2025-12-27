package me.lotiny.misty.bukkit.profile;

import com.google.gson.JsonObject;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.api.profile.stats.StatType;
import me.lotiny.misty.api.profile.stats.Stats;
import me.lotiny.misty.bukkit.storage.StorageSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileSerializer implements StorageSerializer<Profile> {

    @Override
    public String getKey(Profile object) {
        return object.getUniqueId().toString();
    }

    @Override
    public Profile fromJson(JsonObject json) {
        Profile profile = new ProfileImpl(UUID.fromString(get(json, "uniqueId").getAsString()));
        profile.setName(get(json, "name").getAsString());

        Map<StatType, Stats> stats = new HashMap<>();
        JsonObject statsObject = get(json, "stats").getAsJsonObject();
        for (StatType statType : StatType.values()) {
            stats.put(statType, new Stats(statsObject.get(statType.getData()).getAsInt()));
        }
        profile.setStatsMap(stats);

        return profile;
    }

    @Override
    public JsonObject toJson(Profile object) {
        JsonObject json = new JsonObject();
        json.addProperty("uniqueId", object.getUniqueId().toString());
        json.addProperty("name", object.getName());

        JsonObject statsObject = new JsonObject();
        object.getStatsMap().forEach((k, v) -> statsObject.addProperty(k.getData(), v.getAmount()));
        json.add("stats", statsObject);

        return json;
    }

    @Override
    public Profile createDefault(String key) {
        Profile profile = new ProfileImpl(UUID.fromString(key));
        Map<StatType, Stats> stats = new HashMap<>();
        for (StatType statType : StatType.values()) {
            stats.put(statType, statType == StatType.ELO ? new Stats(1000) : new Stats());
        }

        profile.setStatsMap(stats);

        return profile;
    }
}
