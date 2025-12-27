package me.lotiny.misty.bukkit.command.impl;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.command.CommandContext;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.provider.menus.staff.ScenariosAdminMenu;
import me.lotiny.misty.bukkit.provider.menus.user.ScenariosMenu;

@InjectableComponent
@RequiredArgsConstructor
@Command({"scenario", "scenarios", "scen"})
public class ScenarioCommand extends AbstractCommand {

    private final GameManager gameManager;
    private final ScenarioManager scenarioManager;

    @Override
    public void onHelp(CommandContext context) {
        BukkitCommandContext bukkitContext = context.as(BukkitCommandContext.class);
        mustBePlayer(bukkitContext, player -> new ScenariosMenu().open(player));
    }

    @Command(value = {"admin", "list"}, permissionNode = "misty.command.scenario")
    public void onList(BukkitCommandContext context) {
        mustBePlayer(context, player -> new ScenariosAdminMenu().open(player));
    }

    @Command(value = "toggle", permissionNode = "misty.command.scenario")
    public void onToggle(BukkitCommandContext context, Scenario scenario) {
        if (scenario.isEnabled()) {
            scenarioManager.disable(scenario, gameManager, context.getSender(), true);
        } else {
            scenarioManager.enable(scenario, gameManager, context.getSender(), true);
        }
    }
}
