package me.lotiny.misty.command.impl;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.command.MessageType;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.command.AbstractCommand;
import me.lotiny.misty.utils.Message;

@InjectableComponent
@RequiredArgsConstructor
@Command(value = "chat", permissionNode = "misty.command.chat")
public class ChatCommand extends AbstractCommand {

    private final GameManager gameManager;

    @Command("mute")
    public void onMuteChat(BukkitCommandContext context) {
        GameRegistry registry = gameManager.getRegistry();
        boolean isChatMuted = registry.isChatMuted();

        if (isChatMuted) {
            context.sendMessage(MessageType.ERROR, Message.CHAT_ALREADY_MUTE);
        } else {
            context.sendMessage(MessageType.INFO, Message.CHAT_MUTE);
            registry.setChatMuted(true);
        }
    }

    @Command("unmute")
    public void onUnMuteChat(BukkitCommandContext context) {
        GameRegistry registry = gameManager.getRegistry();
        boolean isChatMuted = registry.isChatMuted();

        if (!isChatMuted) {
            context.sendMessage(MessageType.ERROR, Message.CHAT_ALREADY_UNMUTE);
        } else {
            context.sendMessage(MessageType.INFO, Message.CHAT_UNMUTE);
            registry.setChatMuted(false);
        }
    }
}
