package me.lotiny.misty.bukkit.command.impl.team;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.bukkit.util.LegacyAdventureUtil;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.mc.MCPlayer;
import io.fairyproject.util.CC;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameSetting;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@InjectableComponent
@RequiredArgsConstructor
@Command(value = {"lookforteam", "lft", "lookingforteam", "needteammate"}, permissionNode = "misty.command.lft")
public class LookingForTeamCommand extends AbstractCommand {

    private final GameManager gameManager;
    private final ScenarioManager scenarioManager;

    @Command("#")
    public void onCommand(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            GameSetting setting = gameManager.getGame().getSetting();
            GameRegistry registry = gameManager.getRegistry();
            GameState gameState = registry.getState();
            if (setting.getTeamSize() == 1 || scenarioManager.isEnabled("Red vs Blue") || scenarioManager.isEnabled("Love At First Sight")) {
                player.sendMessage(Message.TEAM_DISABLED.toString());
                return;
            }

            if (gameState == GameState.ENDING || gameState == GameState.SCATTERING) {
                player.sendMessage(Message.WRONG_STATE.toString());
                return;
            }

            int maxTeamSize = setting.getTeamSize();
            Team team = UHCUtils.getTeam(player);

            if (team == null) {
                sendLookingForTeamMessage(player);
            } else {
                if (team.getMembers(true).size() >= maxTeamSize) {
                    player.sendMessage(Message.TEAM_FULL.toString());
                    return;
                }

                sendLookingForTeammateMessage(player, maxTeamSize - team.getMembers(true).size());
            }
        });
    }

    private void sendLookingForTeamMessage(Player player) {
        Component component = Component.text()
                .append(LegacyAdventureUtil.decode(CC.CHAT_BAR))
                .appendNewline()
                .append(LegacyAdventureUtil.decode("&2&lLooking For Team!"))
                .appendNewline()
                .append(LegacyAdventureUtil.decode("&b" + player.getName() + "&e is looking for a team!"))
                .appendNewline()
                .append(LegacyAdventureUtil.decode("&7Click to invite this player &a[Invite]"))
                .appendNewline()
                .append(LegacyAdventureUtil.decode(CC.CHAT_BAR))
                .clickEvent(ClickEvent.runCommand("/team invite " + player.getName()))
                .hoverEvent(HoverEvent.showText(Component.text("Click to invite this player to your team", NamedTextColor.GREEN)))
                .asComponent();

        Bukkit.getOnlinePlayers().forEach(online -> {
            MCPlayer mcOnline = MCPlayer.from(online);
            if (mcOnline != null) {
                mcOnline.sendMessage(component);
            }
        });
    }

    private void sendLookingForTeammateMessage(Player player, int remainingSpots) {
        Component component = Component.text()
                .append(LegacyAdventureUtil.decode(CC.CHAT_BAR))
                .appendNewline()
                .append(LegacyAdventureUtil.decode("&2&lLooking For Team!"))
                .appendNewline()
                .append(LegacyAdventureUtil.decode("&b" + player.getName() + "&e" + " is looking for &b" + remainingSpots + "&e more teammate!"))
                .appendNewline()
                .append(LegacyAdventureUtil.decode("&7Click to request to join this team &a[Request]"))
                .appendNewline()
                .append(LegacyAdventureUtil.decode(CC.CHAT_BAR))
                .clickEvent(ClickEvent.runCommand("/team request " + player.getName()))
                .hoverEvent(HoverEvent.showText(Component.text("Click to request to join", NamedTextColor.GREEN)))
                .asComponent();

        Bukkit.getOnlinePlayers().forEach(online -> {
            MCPlayer mcOnline = MCPlayer.from(online);
            if (mcOnline != null) {
                mcOnline.sendMessage(component);
            }
        });
    }
}
