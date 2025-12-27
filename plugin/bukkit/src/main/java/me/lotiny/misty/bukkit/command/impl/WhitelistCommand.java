package me.lotiny.misty.bukkit.command.impl;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.command.CommandContext;
import io.fairyproject.command.MessageType;
import io.fairyproject.command.annotation.Arg;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.command.annotation.CommandPresence;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.util.CC;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.command.MistyPresenceProvider;
import me.lotiny.misty.bukkit.enums.Message;

import java.util.List;

@InjectableComponent
@RequiredArgsConstructor
@Command(value = {"whitelist", "wl"}, permissionNode = "misty.command.whitelist")
@CommandPresence(MistyPresenceProvider.class)
public class WhitelistCommand extends AbstractCommand {

    private final GameManager gameManager;

    @Override
    public void onHelp(CommandContext context) {
        context.sendMessage(
                MessageType.INFO,
                CC.CHAT_BAR,
                "&b/wl add <player> &7- &fAdd player to whitelist",
                "&b/wl remove <player> &7- &fRemove player from whitelist",
                "&b/wl toggle &7- &fToggle enable/disable whitelist",
                "&b/wl list &7- &fList all whitelisted players",
                CC.CHAT_BAR
        );
    }

    @Command("add")
    public void onAdd(BukkitCommandContext context, @Arg("target") String target) {
        List<String> whitelistPlayers = gameManager.getRegistry().getWhitelistPlayers();
        if (whitelistPlayers.contains(target)) {
            context.sendMessage(MessageType.INFO, Message.WHITELIST_PLAYER_ALREADY_WHITELISTED.toString()
                    .replace("<player>", target));
        } else {
            whitelistPlayers.add(target);
            context.sendMessage(MessageType.INFO, Message.WHITELIST_ADD.toString()
                    .replace("<player>", target));
        }
    }

    @Command("remove")
    public void onRemove(BukkitCommandContext context, @Arg("target") String target) {
        List<String> whitelistPlayers = gameManager.getRegistry().getWhitelistPlayers();
        if (whitelistPlayers.remove(target)) {
            context.sendMessage(MessageType.INFO, Message.WHITELIST_REMOVE.toString()
                    .replace("<player>", target));
        } else {
            context.sendMessage(MessageType.INFO, Message.WHITELIST_PLAYER_NOT_WHITELISTED.toString()
                    .replace("<player>", target));
        }
    }

    @Command("toggle")
    public void onToggle(BukkitCommandContext context) {
        GameRegistry registry = gameManager.getRegistry();
        registry.setWhitelist(context.getSender(), !registry.isWhitelist());
    }

    @Command("list")
    public void onList(BukkitCommandContext context) {
        List<String> whitelistPlayers = gameManager.getRegistry().getWhitelistPlayers();
        if (whitelistPlayers.isEmpty()) {
            context.sendMessage(MessageType.INFO, Message.WHITELIST_EMPTY.toString());
        } else {
            StringBuilder message = new StringBuilder();
            message.append(" \n");
            message.append(CC.translate(" &bWhitelisted Players&f:\n"));
            for (String wlPlayer : whitelistPlayers) {
                message.append(CC.translate("&7- &e")).append(wlPlayer).append("\n");
            }
            message.append(" \n");

            context.sendMessage(MessageType.INFO, message.toString());
        }
    }
}
