package me.lotiny.misty.command.impl;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.command.annotation.Arg;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.command.AbstractCommand;
import me.lotiny.misty.utils.Message;
import org.bukkit.entity.Player;

@InjectableComponent
@RequiredArgsConstructor
@Command({"health", "hp", "h"})
public class HealthCommand extends AbstractCommand {

    private final ScenarioManager scenarioManager;

    @Command("#")
    public void onCommand(BukkitCommandContext context, @Arg(value = "target", defaultValue = "self") Player target) {
        mustBePlayer(context, player -> {
            if (scenarioManager.isEnabled("Secret Health")) {
                player.sendMessage(Message.SCENARIO_BLOCK_ACTION.replace("<scenario>", "Secret Health"));
                return;
            }

            sendHealth(player, target);
        });
    }

    private void sendHealth(Player player, Player target) {
        player.sendMessage(Message.HEALTH
                .replace("<target>", target.getName())
                .replace("<health>", String.valueOf(Math.round(target.getHealth()))));
    }
}
