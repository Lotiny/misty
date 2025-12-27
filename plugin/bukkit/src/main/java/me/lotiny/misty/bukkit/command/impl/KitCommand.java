package me.lotiny.misty.bukkit.command.impl;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.command.CommandContext;
import io.fairyproject.command.MessageType;
import io.fairyproject.command.annotation.Arg;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.util.CC;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.kit.Kit;
import me.lotiny.misty.bukkit.kit.KitManager;
import me.lotiny.misty.bukkit.provider.menus.user.KitMenu;

@InjectableComponent
@RequiredArgsConstructor
@Command(value = "kit", permissionNode = "misty.command.kit")
public class KitCommand extends AbstractCommand {

    private final KitManager kitManager;

    @Override
    public void onHelp(CommandContext context) {
        context.sendMessage(
                MessageType.INFO,
                CC.CHAT_BAR,
                "&b/kit gui &7- &fOpen Kit gui",
                "&b/kit default <id> &7- &fSet the kit to default kit",
                CC.CHAT_BAR
        );
    }

    @Command("gui")
    public void onGui(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            new KitMenu().open(player);
        });
    }

    @Command("default")
    public void onDefault(BukkitCommandContext context, @Arg("id") int id) {
        Kit kit = kitManager.getKit(id);
        if (kit == null) {
            context.sendMessage(MessageType.ERROR, "This kit doesn't exist.");
            return;
        }

        kitManager.setDefaultKit(id);
        context.sendMessage(MessageType.INFO, "&aSuccessfully set default kit to Kit #" + id);
    }
}
