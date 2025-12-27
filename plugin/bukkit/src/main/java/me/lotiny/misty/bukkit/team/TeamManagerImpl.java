package me.lotiny.misty.bukkit.team;

import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.util.CC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.api.team.TeamManager;
import me.lotiny.misty.api.team.invitation.TeamInvitation;
import me.lotiny.misty.api.team.invitation.TeamInvitationCooldown;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.storage.StorageRegistry;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@InjectableComponent
@RequiredArgsConstructor
public class TeamManagerImpl implements TeamManager {

    private final StorageRegistry storageRegistry;

    private AtomicInteger counter;

    @Getter
    private Map<Integer, Team> teams;
    @Getter
    private List<TeamInvitation> invitations;
    @Getter
    private TeamInvitationCooldown invitationCooldown;

    @PostInitialize
    public void onPostInit() {
        this.counter = new AtomicInteger(0);
        this.teams = new HashMap<>();
        this.invitations = new ArrayList<>();
        this.invitationCooldown = new TeamInvitationCooldown(30_000L, invitation -> {
            invitations.remove(invitation);
            if (!invitation.isFinished()) {
                invitation.getInviter().sendMessage(CC.translate("&cThe team invite send to " + invitation.getInvited().getName() + " has been expired!"));
                invitation.getInvited().sendMessage(CC.translate("&cThe team invite from " + invitation.getInviter().getName() + " has been expired!"));
            }
        });
    }

    @Override
    public Team createTeam(Player player) {
        return createTeam(counter.getAndIncrement(), player);
    }

    @Override
    public Team createTeam(int id, Player player) {
        if (teams.containsKey(id)) {
            return null;
        }
        return registerTeam(id, player);
    }

    @Override
    public void deleteTeam(Team team) {
        team.getMembers(false).forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                UHCUtils.setTeam(player, null);
            }
        });

        teams.remove(team.getId());
    }

    private Team registerTeam(int id, Player player) {
        Profile profile = storageRegistry.getProfile(player.getUniqueId());
        Team team = new TeamImpl(id, profile);
        UHCUtils.setTeam(player, team);
        player.sendMessage(Message.TEAM_CREATE.toString());
        return team;
    }
}
