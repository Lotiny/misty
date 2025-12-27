package me.lotiny.misty.bukkit.command.impl.team;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.bukkit.util.LegacyAdventureUtil;
import io.fairyproject.command.CommandContext;
import io.fairyproject.command.MessageType;
import io.fairyproject.command.annotation.Arg;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.command.annotation.CommandPresence;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.mc.MCPlayer;
import io.fairyproject.util.CC;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.api.team.TeamManager;
import me.lotiny.misty.api.team.invitation.TeamInvitation;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.command.MistyPresenceProvider;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.team.invitation.TeamInvitationImpl;
import me.lotiny.misty.bukkit.utils.TeamEx;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

@InjectableComponent
@RequiredArgsConstructor
@Command({"team", "t", "party", "p"})
@CommandPresence(MistyPresenceProvider.class)
public class TeamCommand extends AbstractCommand {

    private final GameManager gameManager;
    private final ScenarioManager scenarioManager;
    private final TeamManager teamManager;

    @Override
    public void onHelp(CommandContext context) {
        context.sendMessage(
                MessageType.INFO,
                CC.CHAT_BAR,
                "&b/team create &7- &fCreate team",
                "&b/team invite <player> &7- &fSend team invite to player",
                "&b/team list <player> &7- &fView player's team information",
                "&b/team random &7- &fToggle on/off random teammate",
                "&b/team leave &7- &fLeave from the team",
                "&b/team accept <player> &7- &fAccept and join the team",
                "&b/team request <player> &7- &fSend request to join the team",
                "&b/sendcoords &7- &fSend your coordinate to team member",
                "&b/tc &7- &fToggle team chat",
                CC.CHAT_BAR
        );
    }

    @Command("create")
    public void onCreate(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            if (isTeamDisabled(player)) return;

            if (gameManager.getRegistry().getState() != GameState.LOBBY) {
                player.sendMessage(Message.WRONG_STATE.toString());
                return;
            }

            if (UHCUtils.getTeam(player) != null) {
                player.sendMessage(Message.TEAM_ALREADY_IN_TEAM.toString());
                return;
            }

            teamManager.createTeam(player);
        });
    }

    @Command("invite")
    public void onInvite(BukkitCommandContext context, @Arg("target") Player target) {
        mustBePlayer(context, player -> {
            if (isTeamDisabled(player)) return;

            Team team = UHCUtils.getTeam(player);
            if (team == null) {
                team = teamManager.createTeam(player);
            }

            Team targetTeam = UHCUtils.getTeam(target);
            if (team.getMembers(false).contains(target.getUniqueId()) || targetTeam != null && targetTeam.getMembers(true).size() > 1) {
                player.sendMessage(Message.TEAM_INVITE_CANT.toString());
                return;
            }

            if (team.getMembers(true).size() >= gameManager.getGame().getSetting().getTeamSize()) {
                player.sendMessage(Message.TEAM_FULL.toString());
                return;
            }

            GameRegistry registry = gameManager.getRegistry();
            if (registry.getState() == GameState.INGAME && teamManager.getTeams().size() == 2) {
                player.sendMessage(Message.WRONG_STATE.toString());
                return;
            }

            List<TeamInvitation> invitations = teamManager.getInvitations();
            if (!invitations.isEmpty()) {
                invitations.stream()
                        .filter(invitation -> invitation.getInvited().equals(player) && invitation.getInviter().equals(target))
                        .findFirst()
                        .ifPresent(teamInvitationImpl -> player.sendMessage(Message.TEAM_INVITE_ALREADY_SEND.toString()));
            }

            TeamInvitationImpl invitation = new TeamInvitationImpl(player, target, team);
            invitation.send();

            teamManager.getInvitations().add(invitation);
        });
    }

    @Command("accept")
    public void onAccept(BukkitCommandContext context, @Arg("target") Player target) {
        mustBePlayer(context, player -> {
            if (isTeamDisabled(player)) return;

            Team team = UHCUtils.getTeam(player);
            if (team != null && team.getMembers(true).size() != 1) {
                player.sendMessage(Message.TEAM_ALREADY_IN_TEAM.toString());
                return;
            }

            List<TeamInvitation> invitations = teamManager.getInvitations();
            if (invitations.isEmpty()) {
                player.sendMessage(Message.TEAM_JOIN_FAILED.toString());
                return;
            }

            TeamInvitation teamInvitation = invitations.stream()
                    .filter(invitation -> invitation.getInvited().equals(player) && invitation.getInviter().equals(target))
                    .findFirst()
                    .orElse(null);
            if (teamInvitation == null) {
                player.sendMessage(Message.TEAM_JOIN_FAILED.toString());
                return;
            }

            teamInvitation.accept();
        });
    }

    @Command("leave")
    public void onLeave(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            if (isTeamDisabled(player)) return;

            Team team = UHCUtils.getTeam(player);
            if (team == null) {
                player.sendMessage(Message.TEAM_NOT_IN_TEAM.toString());
                return;
            }

            if (gameManager.getRegistry().getState() != GameState.LOBBY) {
                player.sendMessage(Message.WRONG_STATE.toString());
                return;
            }

            team.removeMember(player);

            if (team.getMembers(true).isEmpty()) {
                teamManager.getTeams().remove(team.getId());
            }
        });
    }

    @Command({"list", "info"})
    public void onList(BukkitCommandContext context, @Arg("target") Player target) {
        mustBePlayer(context, player -> {
            if (gameManager.getGame().getSetting().getTeamSize() == 1) {
                player.sendMessage(Message.TEAM_DISABLED.toString());
                return;
            }

            Team team = UHCUtils.getTeam(target);
            if (team == null) {
                player.sendMessage(Message.TEAM_NOT_IN_TEAM.toString());
                return;
            }

            player.sendMessage(CC.translate("&aTeam #" + team.getId() + " Members:"));
            team.getMembers(false).forEach(uuid -> {
                Player member = Bukkit.getPlayer(uuid);
                if (member != null) {
                    if (UHCUtils.isAlive(uuid)) {
                        player.sendMessage(CC.translate("- &a" + member.getName()));
                    } else {
                        player.sendMessage(CC.translate("- &c" + member.getName()));
                    }
                }
            });
        });
    }

    @Command("random")
    public void onRandom(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            if (isTeamDisabled(player)) return;

            Team team = UHCUtils.getTeam(player);
            if (team == null) {
                player.sendMessage(Message.TEAM_NOT_IN_TEAM.toString());
                return;
            }

            if (gameManager.getRegistry().getState() != GameState.LOBBY) {
                player.sendMessage(Message.WRONG_STATE.toString());
                return;
            }

            boolean isFill = team.getStorage().getOrThrow(TeamEx.TEAM_FILL);
            team.getStorage().put(TeamEx.TEAM_FILL, !isFill);
            player.sendMessage(isFill ? Message.TEAM_RANDOM_DISABLED.toString() : Message.TEAM_RANDOM_ENABLED.toString());
        });
    }

    @Command("request")
    public void onRequest(BukkitCommandContext context, @Arg("target") Player target) {
        mustBePlayer(context, player -> {
            if (isTeamDisabled(player)) return;

            Team team = UHCUtils.getTeam(player);
            if (team.getMembers(true).size() > 1) {
                player.sendMessage(Message.TEAM_ALREADY_IN_TEAM.toString());
                return;
            }

            Team targetTeam = UHCUtils.getTeam(target);
            if (team == targetTeam) return;

            if (targetTeam == null) {
                player.sendMessage(Message.TEAM_NOT_IN_TEAM.toString());
                return;
            }

            if (targetTeam.getMembers(true).size() >= gameManager.getGame().getSetting().getTeamSize()) {
                player.sendMessage(Message.TEAM_FULL.toString());
                return;
            }

            player.sendMessage(CC.translate("&aYou've send request to join " + target.getName() + "'s team!"));

            MCPlayer mcTarget = MCPlayer.from(target);
            if (mcTarget != null) {
                Component component = Component.text()
                        .append(LegacyAdventureUtil.decode(CC.CHAT_BAR))
                        .appendNewline()
                        .append(LegacyAdventureUtil.decode("&b" + player.getName() + "&e is requesting to join your team."))
                        .appendNewline()
                        .append(LegacyAdventureUtil.decode("&7Click to invite this player &a[Invite]"))
                        .appendNewline()
                        .append(LegacyAdventureUtil.decode(CC.CHAT_BAR))
                        .clickEvent(ClickEvent.runCommand("/team invite " + player.getName()))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to invite this player", NamedTextColor.GREEN)))
                        .asComponent();

                mcTarget.sendMessage(component);
            }
        });
    }

    private boolean isTeamDisabled(Player player) {
        if (gameManager.getGame().getSetting().getTeamSize() == 1 || scenarioManager.isEnabled("Red vs Blue") || scenarioManager.isEnabled("Love At First Sight")) {
            player.sendMessage(Message.TEAM_DISABLED.toString());
            return true;
        }
        return false;
    }
}
