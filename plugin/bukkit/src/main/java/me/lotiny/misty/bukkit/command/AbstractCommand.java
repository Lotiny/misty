package me.lotiny.misty.bukkit.command;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.command.BaseCommand;
import io.fairyproject.command.MessageType;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public abstract class AbstractCommand extends BaseCommand {

    protected void mustBePlayer(BukkitCommandContext context, Consumer<Player> consumer) {
        if (!context.isPlayer()) {
            context.sendMessage(MessageType.ERROR, "This command can only be executed by players.");
        } else {
            Player player = context.getPlayer();
            consumer.accept(player);
        }
    }
}
