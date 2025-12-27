package me.lotiny.misty.bukkit.command.parameters;

import io.fairyproject.bukkit.command.parameters.BukkitArgTransformer;
import io.fairyproject.container.InjectableComponent;
import me.lotiny.misty.api.profile.stats.StatType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@InjectableComponent
public class StatTypeArgTransformer extends BukkitArgTransformer<StatType> {

    @Override
    public Class[] type() {
        return new Class[]{StatType.class};
    }

    @Override
    public StatType transform(CommandSender sender, String source) {
        StatType statType = StatType.get(source);
        if (statType == null) {
            return this.fail("No stats with the name " + source + " found.");
        }

        return statType;
    }

    @Override
    public List<String> tabComplete(Player player, String source) {
        return Arrays.stream(StatType.values())
                .map(StatType::getData)
                .toList();
    }
}
