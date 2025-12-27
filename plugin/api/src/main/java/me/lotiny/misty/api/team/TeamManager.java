package me.lotiny.misty.api.team;

import me.lotiny.misty.api.team.invitation.TeamInvitation;
import me.lotiny.misty.api.team.invitation.TeamInvitationCooldown;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public interface TeamManager {

    Map<Integer, Team> getTeams();

    List<TeamInvitation> getInvitations();

    TeamInvitationCooldown getInvitationCooldown();

    Team createTeam(Player player);

    Team createTeam(int id, Player player);

    void deleteTeam(Team team);
}
