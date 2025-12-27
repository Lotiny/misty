package me.lotiny.misty.bukkit.hook.impl.apollo;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.location.ApolloLocation;
import com.lunarclient.apollo.module.team.TeamMember;
import com.lunarclient.apollo.module.team.TeamModule;
import io.fairyproject.log.Log;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.bukkit.hook.PluginHook;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ApolloHook implements PluginHook {

    private TeamModule teamModule;

    @Override
    public void register() {
        this.teamModule = Apollo.getModuleManager().getModule(TeamModule.class);

        Log.info("Hooked 'Apollo' for LunarClient support.");
    }

    public void refreshTeam(Team team) {
        List<TeamMember> teammates = team.getMembers(true).stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .map(this::createTeamMember)
                .collect(Collectors.toList());

        team.getMembers(true).forEach(uuid -> Apollo.getPlayerManager().getPlayer(uuid)
                .ifPresent(apolloPlayer -> teamModule.updateTeamMembers(apolloPlayer, teammates)));
    }

    private TeamMember createTeamMember(Player member) {
        Location location = member.getLocation();

        return TeamMember.builder()
                .playerUuid(member.getUniqueId())
                .displayName(Component.text()
                        .content(member.getName())
                        .color(NamedTextColor.WHITE)
                        .build())
                .markerColor(Color.GREEN)
                .location(ApolloLocation.builder()
                        .world(location.getWorld().getName())
                        .x(location.getX())
                        .y(location.getY())
                        .z(location.getZ())
                        .build())
                .build();
    }
}
