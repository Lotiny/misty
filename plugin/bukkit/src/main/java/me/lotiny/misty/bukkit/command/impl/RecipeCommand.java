package me.lotiny.misty.bukkit.command.impl;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.provider.menus.user.recipes.ItemRecipeMenu;

@InjectableComponent
@RequiredArgsConstructor
@Command({"recipe", "recipes"})
public class RecipeCommand extends AbstractCommand {

    private final GameManager gameManager;
    private final ScenarioManager scenarioManager;

    @Command("#")
    public void onCommand(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            if (!scenarioManager.isEnabled("Custom Craft")) {
                player.sendMessage(Message.SCENARIO_NOT_ENABLED.toString()
                        .replace("<scenario>", "Custom Craft"));
                return;
            }

            if (gameManager.getRegistry().getState() != GameState.INGAME) {
                player.sendMessage(Message.WRONG_STATE.toString());
                return;
            }

            new ItemRecipeMenu().open(player);
        });
    }
}
