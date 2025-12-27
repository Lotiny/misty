package me.lotiny.misty.bukkit.team;

import io.fairyproject.container.Autowired;
import io.fairyproject.data.MetaStorage;
import io.fairyproject.util.CC;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.api.team.TeamManager;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.storage.StorageRegistry;
import me.lotiny.misty.bukkit.utils.TeamEx;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TeamImpl implements Team {

    @Autowired
    private static StorageRegistry storageRegistry;
    @Autowired
    private static GameManager gameManager;
    @Autowired
    private static TeamManager teamManager;

    private int id;
    private Profile leader;
    private List<UUID> members;
    private MetaStorage storage;

    public TeamImpl(int id, Profile leader) {
        this.id = id;
        this.leader = leader;
        this.members = new ArrayList<>(List.of(leader.getUniqueId()));
        this.storage = MetaStorage.create();
        this.storage.put(TeamEx.TEAM_FILL, true);
        this.storage.put(TeamEx.TEAM_KILLS, 0);
        //noinspection deprecation
        this.storage.put(TeamEx.TEAM_INVENTORY, Bukkit.createInventory(null, 27, "Team #" + id));
        teamManager.getTeams().put(id, this);
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public Profile getLeader() {
        return this.leader;
    }

    @Override
    public void setLeader(Player player) {
        this.leader = storageRegistry.getProfile(player.getUniqueId());
    }

    @Override
    public List<UUID> getMembers(boolean onlyAlive) {
        if (onlyAlive) {
            return this.members.stream()
                    .filter(UHCUtils::isAlive)
                    .collect(Collectors.toList());
        } else {
            return this.members;
        }
    }

    @Override
    public MetaStorage getStorage() {
        return this.storage;
    }

    @Override
    public int getTeamKills() {
        return this.storage.getOrThrow(TeamEx.TEAM_KILLS);
    }

    @Override
    public void setTeamKills(int amount) {
        this.storage.put(TeamEx.TEAM_KILLS, amount);
    }

    @Override
    public void addMember(Player player) {
        this.members.add(player.getUniqueId());
        UHCUtils.setTeam(player, this);

        if (gameManager.getRegistry().getState() == GameState.INGAME) {
            setTeamKills(UHCUtils.getGameKills(player));
        }

        sendMessage(Message.TEAM_MEMBER_JOINED.toString()
                .replace("<player>", player.getName()));
    }

    @Override
    public void removeMember(Player player) {
        sendMessage(Message.TEAM_MEMBER_LEFT.toString()
                .replace("<player>", player.getName()));

        this.members.remove(player.getUniqueId());
        UHCUtils.setTeam(player, null);
    }

    @Override
    public void sendMessage(String message) {
        for (UUID member : this.members) {
            Player player = Bukkit.getPlayer(member);
            if (player != null) {
                player.sendMessage(CC.translate(message));
            }
        }
    }

    @Override
    public boolean isSame(Team team) {
        return this.id == team.getId();
    }
}
