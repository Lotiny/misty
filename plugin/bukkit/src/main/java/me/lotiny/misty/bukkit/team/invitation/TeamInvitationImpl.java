package me.lotiny.misty.bukkit.team.invitation;

import io.fairyproject.bukkit.util.LegacyAdventureUtil;
import io.fairyproject.container.Autowired;
import io.fairyproject.mc.MCPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.api.team.TeamManager;
import me.lotiny.misty.api.team.invitation.TeamInvitation;
import me.lotiny.misty.bukkit.enums.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class TeamInvitationImpl implements TeamInvitation {

    @Autowired
    private static TeamManager teamManager;

    private final Player inviter;
    private final Player invited;
    private final Team team;

    private boolean finished = false;

    @Override
    public void send() {
        teamManager.getInvitationCooldown().addCooldown(this);
        inviter.sendMessage(Message.TEAM_INVITE_SEND.toString()
                .replace("<invited>", invited.getName())
                .replace("<inviter>", inviter.getName()));

        MCPlayer mcInvited = MCPlayer.from(invited);
        if (mcInvited != null) {
            Component component = Component.text()
                    .append(LegacyAdventureUtil.decode(Message.TEAM_INVITE_RECEIVED.toString()
                            .replace("<invited>", invited.getName())
                            .replace("<inviter>", inviter.getName())
                    ))
                    .clickEvent(ClickEvent.runCommand("/team accept " + inviter.getName()))
                    .hoverEvent(Component.text("Click to join", NamedTextColor.GREEN))
                    .asComponent();

            mcInvited.sendMessage(component);
        }
    }

    @Override
    public void accept() {
        if (!teamManager.getInvitationCooldown().isCooldown(this)) {
            invited.sendMessage(Message.TEAM_JOIN_FAILED.toString());
            return;
        }

        finished = true;
        team.addMember(invited);
    }
}
