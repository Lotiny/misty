package me.lotiny.misty.bukkit.manager.leaderboard;

import com.google.gson.JsonObject;
import io.fairyproject.container.Autowired;
import io.fairyproject.mc.util.Position;
import me.lotiny.misty.api.profile.stats.StatType;
import me.lotiny.misty.bukkit.storage.StorageSerializer;

public class LeaderboardHologramSerializer implements StorageSerializer<LeaderboardHologram> {

    @Autowired
    private static LeaderboardManager leaderboardManager;

    @Override
    public String getKey(LeaderboardHologram object) {
        return object.getLeaderboard().getStatType().getData();
    }

    @Override
    public LeaderboardHologram fromJson(JsonObject json) {
        StatType statType = StatType.get(get(json, "leaderboardType").getAsString());
        Leaderboard leaderboard = leaderboardManager.getLeaderboardMap().get(statType);
        Position position = Position.fromString(get(json, "position").getAsString());

        return new LeaderboardHologram(leaderboard, position);
    }

    @Override
    public JsonObject toJson(LeaderboardHologram object) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("leaderboardType", object.getLeaderboard().getStatType().getData());
        jsonObject.addProperty("position", object.getPosition().toString());

        return jsonObject;
    }

    @Override
    public LeaderboardHologram createDefault(String key) {
        return new LeaderboardHologram(leaderboardManager.getLeaderboardMap().get(StatType.get(key)), null);
    }
}
