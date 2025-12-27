package me.lotiny.misty.bukkit.command.impl;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.bukkit.util.Players;
import io.fairyproject.command.annotation.Arg;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.mc.scheduler.MCSchedulers;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.event.PlayerScatterEvent;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.api.team.TeamManager;
import me.lotiny.misty.bukkit.Permission;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@InjectableComponent
@RequiredArgsConstructor
@Command("latescatter")
public class LateScatterCommand extends AbstractCommand {

    private final GameManager gameManager;
    private final TeamManager teamManager;
    private final ScenarioManager scenarioManager;

    @Command("#")
    public void onCommand(BukkitCommandContext context, @Arg(value = "target", defaultValue = "self") Player target) {
        mustBePlayer(context, player -> {
            GameRegistry registry = gameManager.getRegistry();
            if (!GameState.INGAME.equals(registry.getState())) {
                player.sendMessage(Message.WRONG_STATE.toString());
                return;
            }

            if (!gameManager.getGame().getSetting().isLateScatter()) {
                player.sendMessage(Message.LATE_SCATTER_DISABLED.toString());
                return;
            }

            UUID uuid = target.getUniqueId();
            if (UHCUtils.isAlive(uuid) || registry.getPlayers().containsKey(uuid) ||
                    (player != target && !player.hasPermission(Permission.LATE_SCATTER_OTHER)) ||
                    (gameManager.getRegistry().isPvpEnabled() && !player.hasPermission(Permission.LATE_SCATTER_BYPASS))) {
                player.sendMessage(Message.LATE_SCATTER_CANT.toString());
                return;
            }

            Players.clear(player);
            registry.getPlayers().put(uuid, true);

            UHCUtils.giveStarterItem(target);
            MCSchedulers.getGlobalScheduler().schedule(() -> Bukkit.getPluginManager().callEvent(new PlayerScatterEvent(target, true)));
            if (scenarioManager.isEnabled("Red vs Blue")) {
                Team team0 = teamManager.getTeams().get(0);
                Team team1 = teamManager.getTeams().get(1);

                Team smallerTeam = team0.getMembers(true).size() > team1.getMembers(true).size() ? team1 : team0;
                smallerTeam.addMember(target);
            } else {
                Team team = UHCUtils.getTeam(target);
                if (team == null) {
                    teamManager.createTeam(target);
                } else {
                    teamManager.getTeams().put(team.getId(), team);
                }
            }

            gameManager.teleportToRandomLocation(target, gameManager.getGame().getSetting().getBorderSize());
        });
    }
}
