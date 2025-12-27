package me.lotiny.misty.api.team.invitation;

import me.lotiny.misty.api.team.Team;
import org.bukkit.entity.Player;

public interface TeamInvitation {

    Player getInviter();

    Player getInvited();

    Team getTeam();

    boolean isFinished();

    void send();

    void accept();
}
