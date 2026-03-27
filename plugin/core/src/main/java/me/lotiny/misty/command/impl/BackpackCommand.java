package me.lotiny.misty.command.impl;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.command.AbstractCommand;
import me.lotiny.misty.utils.Message;
import me.lotiny.misty.utils.TeamEx;
import me.lotiny.misty.utils.UHCUtils;

@InjectableComponent
@RequiredArgsConstructor
@Command({"backpack", "bp"})
public class BackpackCommand extends AbstractCommand {

    private final GameManager gameManager;
    private final ScenarioManager scenarioManager;

    @Command("#")
    public void onCommand(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            if (!scenarioManager.isEnabled("Backpacks")) {
                player.sendMessage(Message.SCENARIO_NOT_ENABLED.replace("<scenario>", "Backpacks"));
                return;
            }

            if (gameManager.getRegistry().getState() != GameState.INGAME) {
                player.sendMessage(Message.WRONG_STATE);
                return;
            }

            Team team = UHCUtils.getTeam(player);
            if (team == null) {
                player.sendMessage(Message.TEAM_NOT_IN_TEAM);
                return;
            }

            player.openInventory(team.getStorage().getOrThrow(TeamEx.TEAM_INVENTORY));
        });
    }
}
