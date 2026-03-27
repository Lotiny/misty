package me.lotiny.misty.command.impl.team;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.metadata.MetadataMap;
import io.fairyproject.util.CC;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.command.AbstractCommand;
import me.lotiny.misty.utils.Message;
import me.lotiny.misty.utils.TeamEx;
import me.lotiny.misty.utils.UHCUtils;

@InjectableComponent
@RequiredArgsConstructor
@Command({"teamchat", "tc", "partychat", "pc"})
public class TeamChatCommand extends AbstractCommand {

    private final GameManager gameManager;

    @Command("#")
    public void onCommand(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            if (gameManager.getGame().getSetting().getTeamSize() == 1) {
                player.sendMessage(CC.translate(Message.TEAM_DISABLED));
                return;
            }

            Team team = UHCUtils.getTeam(player);
            if (team == null) {
                player.sendMessage(Message.TEAM_NOT_IN_TEAM);
                return;
            }

            String[] args = context.getArgs();
            if (args.length > 0) {
                String message = String.join(" ", args);
                team.sendMessage(message);
                return;
            }

            MetadataMap meta = Metadata.provideForPlayer(player);
            boolean isTeamChat = meta.getOrDefault(TeamEx.TEAM_CHAT, false);
            meta.put(TeamEx.TEAM_CHAT, !isTeamChat);
            if (isTeamChat) {
                player.sendMessage(Message.TEAM_TOGGLE_TEAMCHAT_DISABLED);
            } else {
                player.sendMessage(Message.TEAM_TOGGLE_TEAMCHAT_ENABLED);
            }
        });
    }
}
