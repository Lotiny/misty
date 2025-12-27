package me.lotiny.misty.bukkit.command.impl;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.command.CommandContext;
import io.fairyproject.command.MessageType;
import io.fairyproject.command.annotation.Arg;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.command.annotation.CommandPresence;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.util.CC;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.api.team.TeamManager;
import me.lotiny.misty.bukkit.Permission;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.command.MistyPresenceProvider;
import me.lotiny.misty.bukkit.storage.StorageRegistry;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import me.lotiny.misty.bukkit.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@InjectableComponent
@RequiredArgsConstructor
@Command({"misty", "fo32jf02834jv3nnwffwe78"})
@CommandPresence(MistyPresenceProvider.class)
public class MistyCommand extends AbstractCommand {

    private final GameManager gameManager;
    private final TeamManager teamManager;
    private final StorageRegistry storageRegistry;

    @Override
    public void onHelp(CommandContext context) {
        context.sendMessage(
                MessageType.INFO,
                " ",
                "&aMisty &fversion &a" + VersionUtils.getPluginVersion(),
                "&fAuthor: &aLotiny",
                "&fWebsite: &ahttps://github.com/Lotiny",
                " "
        );
    }

    @Command(value = "teaminfo", permissionNode = Permission.HOST_PERMISSION)
    public void onTeamInfo(BukkitCommandContext context, @Arg("target") Player target) {
        mustBePlayer(context, player -> {
            Team team = UHCUtils.getTeam(target);
            if (team == null) {
                player.sendMessage(CC.translate("&c" + target.getName() + " is not in a team!"));
                return;
            }

            player.sendMessage("Team #" + team.getId());
            team.getMembers(false).forEach(uuid -> {
                Player member = Bukkit.getPlayer(uuid);
                if (member != null) {
                    player.sendMessage("- " + member.getName() + " (" + member.getUniqueId() + ")");
                }
            });
        });
    }

    @Command(value = "listteam", permissionNode = Permission.HOST_PERMISSION)
    public void onListTeam(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            Map<Integer, Team> teams = teamManager.getTeams();

            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.translate("&aTotal Team &7= &b" + teams.size()));
            teams.values().forEach(team -> {
                player.sendMessage(CC.translate("&7- &2Team #" + team.getId()));
            });
            player.sendMessage(CC.CHAT_BAR);
        });
    }

    @Command(value = "listalive", permissionNode = Permission.HOST_PERMISSION)
    public void onListAlive(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            List<UUID> players = gameManager.getRegistry().getAlivePlayers();

            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.translate("&aTotal Alive &7= &b" + players.size()));
            sendList(player, players);
        });
    }

    @Command(value = "listspec", permissionNode = Permission.HOST_PERMISSION)
    public void onListSpec(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            List<UUID> players = gameManager.getRegistry().getSpectators();

            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.translate("&aTotal Spectators &7= &b" + players.size()));
            sendList(player, players);
        });
    }

    @Command(value = "checkwinner", permissionNode = Permission.HOST_PERMISSION)
    public void onCheckWinner(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            if (!gameManager.checkWinner()) {
                player.sendMessage(CC.translate("&cGame is not ended. Use '/misty listteam' to check alive teams."));
            }
        });
    }

    private void sendList(Player player, List<UUID> players) {
        players.forEach(uuid -> {
            Player target = Bukkit.getPlayer(uuid);
            if (target == null) {
                Profile profile = storageRegistry.getProfile(uuid);
                player.sendMessage(CC.translate("&7- &4" + profile.getName()));
            } else {
                player.sendMessage(CC.translate("&7- &2" + target.getName()));
            }
        });
        player.sendMessage(CC.CHAT_BAR);
    }
}
