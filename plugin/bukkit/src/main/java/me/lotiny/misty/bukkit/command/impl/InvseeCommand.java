package me.lotiny.misty.bukkit.command.impl;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.command.annotation.Arg;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.util.CC;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.provider.menus.staff.InspectMenu;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.entity.Player;

@InjectableComponent
@RequiredArgsConstructor
@Command(value = "invsee", permissionNode = "misty.command.invsee")
public class InvseeCommand extends AbstractCommand {

    @Command("#")
    public void onCommand(BukkitCommandContext context, @Arg("target") Player target) {
        mustBePlayer(context, player -> {
            if (!UHCUtils.isAlive(player.getUniqueId())) {
                player.sendMessage(CC.translate("&cYou must be in spectator mode to use this command."));
                return;
            }

            new InspectMenu(target).open(player);
        });
    }
}
