package me.lotiny.misty.bukkit.utils;

import io.fairyproject.bukkit.util.BukkitPos;
import io.fairyproject.mc.util.Position;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.impl.MainConfig;
import me.lotiny.misty.bukkit.config.impl.PracticeConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public enum LocationEx {

    PRACTICE, LOBBY;

    public Position getPosition() {
        MainConfig mainConfig = Config.getMainConfig();
        PracticeConfig practiceConfig = Config.getPracticeConfig();
        if (practiceConfig == null || mainConfig == null) {
            throw new IllegalStateException("Configs not initialized yet!");
        }

        return switch (this) {
            case PRACTICE -> BukkitPos.toMCPos(practiceConfig.getLocation());
            case LOBBY -> BukkitPos.toMCPos(mainConfig.getLobbyLocation());
        };
    }

    public Location getLocation() {
        return BukkitPos.toBukkitLocation(getPosition());
    }

    public void teleport(Player player) {
        player.teleport(getLocation());
    }
}
