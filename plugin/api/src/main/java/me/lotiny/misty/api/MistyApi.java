package me.lotiny.misty.api;

import me.lotiny.misty.api.customitem.CustomItem;
import me.lotiny.misty.api.customitem.CustomItemRegistry;
import me.lotiny.misty.api.profile.stats.StatType;
import me.lotiny.misty.api.profile.stats.Stats;
import me.lotiny.misty.api.team.Team;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public interface MistyApi {

    static MistyApi getInstance() {
        return MistyApiProvider.get();
    }

    int getTeamSize();

    int getFinalHeal();

    int getGracePeriod();

    int getBorder();

    int getFirstShrink();

    int getNetherTime();

    int getAppleRate();

    boolean isTeamGame();

    Team getTeam(Player player);

    Stats getPlayerStats(Player player, StatType statType);

    boolean isPlaying(UUID uniqueId);

    default boolean isPlaying(Player player) {
        return player != null && isPlaying(player.getUniqueId());
    }

    boolean isSpectator(UUID uniqueId);

    default boolean isSpectator(Player player) {
        return player != null && isSpectator(player.getUniqueId());
    }

    CustomItemRegistry getCustomItemRegistry();

    default void registerCustomItem(CustomItem customItem) {
        getCustomItemRegistry().register(customItem);
    }
}
