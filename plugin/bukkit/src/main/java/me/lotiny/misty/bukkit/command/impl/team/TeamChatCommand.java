package me.lotiny.misty.bukkit.command.impl.team;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.metadata.MetadataMap;
import io.fairyproject.util.CC;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.TeamEx;
import me.lotiny.misty.bukkit.utils.UHCUtils;

@InjectableComponent
@RequiredArgsConstructor
@Command({"teamchat", "tc", "partychat", "pc"})
public class TeamChatCommand extends AbstractCommand {

    private final GameManager gameManager;

    @Command("#")
    public void onCommand(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            if (gameManager.getGame().getSetting().getTeamSize() == 1) {
                player.sendMessage(CC.translate(Message.TEAM_DISABLED.toString()));
                return;
            }

            Team team = UHCUtils.getTeam(player);
            if (team == null) {
                player.sendMessage(Message.TEAM_NOT_IN_TEAM.toString());
                return;
            }

            MetadataMap meta = Metadata.provideForPlayer(player);
            boolean isTeamChat = meta.getOrDefault(TeamEx.TEAM_CHAT, false);
            meta.put(TeamEx.TEAM_CHAT, !isTeamChat);
            if (isTeamChat) {
                player.sendMessage(Message.TEAM_TOGGLE_TEAMCHAT_DISABLED.toString());
            } else {
                player.sendMessage(Message.TEAM_TOGGLE_TEAMCHAT_ENABLED.toString());
            }
        });
    }
}
