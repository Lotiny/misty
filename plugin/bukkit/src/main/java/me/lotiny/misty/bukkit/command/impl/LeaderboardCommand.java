package me.lotiny.misty.bukkit.command.impl;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.provider.menus.user.LeaderboardMenu;

@InjectableComponent
@Command("leaderboards")
public class LeaderboardCommand extends AbstractCommand {

    @Command("#")
    public void onCommand(BukkitCommandContext context) {
        mustBePlayer(context, player -> new LeaderboardMenu().open(player));
    }
}
