package me.lotiny.misty.api.team;

import io.fairyproject.data.MetaStorage;
import me.lotiny.misty.api.profile.Profile;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface Team {

    int getId();

    Profile getLeader();

    void setLeader(Player player);

    List<UUID> getMembers(boolean onlyAlive);

    MetaStorage getStorage();

    int getTeamKills();

    void setTeamKills(int amount);

    void addMember(Player player);

    void removeMember(Player player);

    void sendMessage(String message);

    boolean isSame(Team team);
}
