package me.lotiny.misty.bukkit.command.impl;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.provider.menus.staff.configuration.ConfigMenu;

@InjectableComponent
@RequiredArgsConstructor
@Command({"config", "uhcconfig", "configuration", "uhcconfiguration"})
public class ConfigCommand extends AbstractCommand {

    private final GameManager gameManager;

    @Command("#")
    public void onCommand(BukkitCommandContext context) {
        mustBePlayer(context, player -> new ConfigMenu(gameManager.getGame().getSetting()).open(player));
    }
}
