package me.lotiny.misty.bukkit.command.parameters;

import io.fairyproject.bukkit.command.parameters.BukkitArgTransformer;
import io.fairyproject.container.InjectableComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@InjectableComponent
public class StringArgTransformer extends BukkitArgTransformer<String> {

    @Override
    public Class[] type() {
        return new Class[]{String.class};
    }

    @Override
    public String transform(CommandSender sender, String source) {
        if (sender instanceof Player && source.equals("self")) {
            return sender.getName();
        }

        return source;
    }

    @Override
    public List<String> tabComplete(Player player, String source) {
        return Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .toList();
    }
}
