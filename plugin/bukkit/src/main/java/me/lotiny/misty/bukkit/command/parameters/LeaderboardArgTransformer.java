package me.lotiny.misty.bukkit.command.parameters;

import io.fairyproject.bukkit.command.parameters.BukkitArgTransformer;
import io.fairyproject.container.InjectableComponent;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.profile.stats.StatType;
import me.lotiny.misty.bukkit.manager.leaderboard.Leaderboard;
import me.lotiny.misty.bukkit.manager.leaderboard.LeaderboardManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@InjectableComponent
@RequiredArgsConstructor
public class LeaderboardArgTransformer extends BukkitArgTransformer<Leaderboard> {

    private final LeaderboardManager leaderboardManager;

    @Override
    public Class[] type() {
        return new Class[]{Leaderboard.class};
    }

    @Override
    public Leaderboard transform(CommandSender sender, String source) {
        StatType statType = StatType.get(source);
        if (statType == null) {
            return this.fail("No leaderboard with the name " + source + " found.");
        }

        return leaderboardManager.getLeaderboardMap().get(statType);
    }

    @Override
    public List<String> tabComplete(Player player, String source) {
        return leaderboardManager.getLeaderboardMap()
                .values()
                .stream()
                .map(Leaderboard::getData)
                .toList();
    }
}
