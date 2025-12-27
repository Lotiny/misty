package me.lotiny.misty.bukkit.command;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.command.MessageType;
import io.fairyproject.command.PresenceProvider;
import io.fairyproject.util.CC;

public class MistyPresenceProvider implements PresenceProvider<BukkitCommandContext> {

    @Override
    public Class<BukkitCommandContext> type() {
        return BukkitCommandContext.class;
    }

    @Override
    public void sendMessage(BukkitCommandContext commandContext, MessageType messageType, String... messages) {
        String color;

        switch (messageType) {
            case WARN -> color = CC.GOLD;
            case ERROR -> color = CC.RED;
            default -> color = CC.AQUA;
        }

        for (String message : messages) {
            commandContext.getSender().sendMessage(color + CC.translate(message));
        }
    }
}
