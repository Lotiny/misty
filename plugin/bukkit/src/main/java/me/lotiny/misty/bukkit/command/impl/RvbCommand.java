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
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.api.team.TeamManager;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.command.MistyPresenceProvider;
import me.lotiny.misty.bukkit.enums.Message;
import org.bukkit.entity.Player;

@InjectableComponent
@RequiredArgsConstructor
@Command(value = "rvb", permissionNode = "misty.command.rvb")
@CommandPresence(MistyPresenceProvider.class)
public class RvbCommand extends AbstractCommand {

    private static final String RED_VS_BLUE = "Red vs Blue";
    private static final String SCENARIO_PLACEHOLDER = "<scenario>";
    private final TeamManager teamManager;
    private final ScenarioManager scenarioManager;
    private String redCaptainName = "None";
    private String blueCaptainName = "None";

    @Override
    public void onHelp(CommandContext context) {
        context.sendMessage(
                MessageType.INFO,
                CC.CHAT_BAR,
                "&b/rvb red <player> &7- &fAssign a player to be the red team captain",
                "&b/rvb blue <player> &7- &fAssign a player to be the blue team captain",
                "&b/rvb reset &7- &fReset the team captains",
                "&b/rvb info &7- &fGet information about the team captains",
                CC.CHAT_BAR
        );
    }

    @Command("red")
    public void onRed(BukkitCommandContext context, @Arg("player") Player player) {
        if (!scenarioManager.isEnabled(RED_VS_BLUE)) {
            context.sendMessage(MessageType.ERROR, Message.SCENARIO_NOT_ENABLED.toString()
                    .replace(SCENARIO_PLACEHOLDER, RED_VS_BLUE));
            return;
        }

        Team redTeam = teamManager.createTeam(0, player);
        if (redTeam == null) {
            context.sendMessage(MessageType.ERROR, Message.RVB_ALREADY_HAVE_CAPTAIN_RED.toString());
            return;
        }

        redCaptainName = player.getName();
        context.sendMessage(MessageType.INFO, Message.RVB_ASSIGN_CAPTAIN_RED.toString()
                .replace("<player>", player.getName()));
    }

    @Command("blue")
    public void onBlue(BukkitCommandContext context, @Arg("player") Player player) {
        if (!scenarioManager.isEnabled(RED_VS_BLUE)) {
            context.sendMessage(MessageType.ERROR, Message.SCENARIO_NOT_ENABLED.toString()
                    .replace(SCENARIO_PLACEHOLDER, RED_VS_BLUE));
            return;
        }

        Team blueTeam = teamManager.createTeam(1, player);
        if (blueTeam == null) {
            context.sendMessage(MessageType.ERROR, Message.RVB_ALREADY_HAVE_CAPTAIN_BLUE.toString());
            return;
        }

        blueCaptainName = player.getName();
        context.sendMessage(MessageType.INFO, Message.RVB_ASSIGN_CAPTAIN_BLUE.toString()
                .replace("<player>", player.getName()));
    }

    @Command("reset")
    public void onRemove(BukkitCommandContext context) {
        if (!scenarioManager.isEnabled(RED_VS_BLUE)) {
            context.sendMessage(MessageType.ERROR, Message.SCENARIO_NOT_ENABLED.toString()
                    .replace(SCENARIO_PLACEHOLDER, RED_VS_BLUE));
            return;
        }

        teamManager.getTeams().values().forEach(teamManager::deleteTeam);
        redCaptainName = "None";
        blueCaptainName = "None";
        context.sendMessage(MessageType.INFO, Message.RVB_RESET_CAPTAINS.toString());
    }

    @Command("info")
    public void onInfo(BukkitCommandContext context) {
        if (!scenarioManager.isEnabled(RED_VS_BLUE)) {
            context.sendMessage(MessageType.ERROR, Message.SCENARIO_NOT_ENABLED.toString()
                    .replace(SCENARIO_PLACEHOLDER, RED_VS_BLUE));
            return;
        }

        context.sendMessage(
                MessageType.INFO,
                CC.CHAT_BAR,
                "&bRed Team Captain: &f" + redCaptainName,
                "&bBlue Team Captain: &f" + blueCaptainName,
                CC.CHAT_BAR
        );
    }
}
