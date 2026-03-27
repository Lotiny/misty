package me.lotiny.misty.command.impl;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.command.AbstractCommand;
import me.lotiny.misty.provider.menus.staff.DisqualifyMenu;
import me.lotiny.misty.utils.Message;

@InjectableComponent
@RequiredArgsConstructor
@Command(value = "disqualify", permissionNode = "misty.command.disqualify")
public class DisqualifyCommand extends AbstractCommand {

    private final GameManager gameManager;

    @Command("#")
    public void onCommand(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            if (gameManager.getRegistry().getState() != GameState.INGAME) {
                player.sendMessage(Message.WRONG_STATE);
                return;
            }

            new DisqualifyMenu().open(player);
        });
    }
}
