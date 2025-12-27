package me.lotiny.misty.bukkit.utils;

import io.fairyproject.data.MetaKey;
import io.fairyproject.metadata.MetadataKey;
import lombok.experimental.UtilityClass;
import me.lotiny.misty.api.profile.stats.Stats;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.bukkit.utils.cooldown.CombatCooldown;

@UtilityClass
public class KeyEx {

    // Snapshot
    public MetadataKey<Snapshot> SNAPSHOT_KEY = MetadataKey.create("misty:snapshot-key", Snapshot.class);
    // Game Kills
    public MetadataKey<Stats> GAME_KILLS_KEY = MetadataKey.create("misty:game-kills-key", Stats.class);
    // Combat
    public MetaKey<CombatCooldown> COMBAT_COOLDOWN_KEY = MetaKey.create("misty:combat-cooldown-key", CombatCooldown.class);
    // Team
    public MetadataKey<Team> TEAM_KEY = MetadataKey.create("misty:team-key", Team.class);
}
