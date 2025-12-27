package me.lotiny.misty.bukkit.command.impl.team;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.UHCUtils;

@InjectableComponent
@RequiredArgsConstructor
@Command({"sendcoords", "coords", "scs"})
public class SendCoordCommand extends AbstractCommand {

    @Command("#")
    public void onCommand(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            Team team = UHCUtils.getTeam(player);

            if (team == null) {
                player.sendMessage(Message.TEAM_NOT_IN_TEAM.toString());
                return;
            }

            team.sendMessage(Message.TEAM_SEND_COORDS.toString()
                    .replace("<player>", player.getName())
                    .replace("<x>", String.valueOf(player.getLocation().getBlockX()))
                    .replace("<y>", String.valueOf(player.getLocation().getBlockY()))
                    .replace("<z>", String.valueOf(player.getLocation().getBlockZ())));
        });
    }
}
