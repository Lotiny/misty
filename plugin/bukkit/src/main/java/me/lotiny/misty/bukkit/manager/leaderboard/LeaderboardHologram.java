package me.lotiny.misty.bukkit.manager.leaderboard;

import io.fairyproject.bukkit.util.BukkitPos;
import io.fairyproject.bukkit.util.LegacyAdventureUtil;
import io.fairyproject.mc.hologram.Hologram;
import io.fairyproject.mc.hologram.line.HologramLine;
import io.fairyproject.mc.util.Position;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LeaderboardHologram {

    private final Leaderboard leaderboard;

    private Position position;
    private Hologram hologram;

    public LeaderboardHologram(Leaderboard leaderboard, Position position) {
        this.leaderboard = leaderboard;
        this.position = position;
    }

    public void spawn() {
        List<HologramLine> lines = new ArrayList<>();
        lines.add(HologramLine.create(LegacyAdventureUtil.decode("&b" + leaderboard.getDisplayName())));
        lines.add(HologramLine.create(Component.empty()));
        leaderboard.getPlayers().forEach((place, player) -> {
            String format = "&9" + place + " &e" + player.getName() + "&7 - &b" + player.getValue();
            lines.add(HologramLine.create(LegacyAdventureUtil.decode(format)));
        });

        this.hologram = Hologram.create(position)
                .lines(lines)
                .spawn();
    }

    public void remove() {
        if (this.hologram != null) {
            this.hologram.remove();
        }
    }

    public void move(Location location) {
        if (hologram == null) {
            throw new NullPointerException(leaderboard.getStatType().getData() + " hologram doesn't exist");
        }

        position = BukkitPos.toMCPos(location);

        hologram.remove();
        spawn();
    }
}
