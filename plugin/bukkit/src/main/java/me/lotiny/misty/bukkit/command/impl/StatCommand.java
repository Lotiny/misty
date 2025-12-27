package me.lotiny.misty.bukkit.command.impl;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.command.annotation.Arg;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.provider.menus.user.StatsMenu;
import me.lotiny.misty.bukkit.storage.StorageRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@InjectableComponent
@RequiredArgsConstructor
@Command({"stats", "elo"})
public class StatCommand extends AbstractCommand {

    private final StorageRegistry storageRegistry;

    @Command("#")
    public void onCommand(BukkitCommandContext context, @Arg(value = "target", defaultValue = "self") Profile profile) {
        mustBePlayer(context, player -> {
            new StatsMenu(profile).open(player);

            Player target = Bukkit.getPlayer(profile.getUniqueId());
            if (target == null) {
                storageRegistry.getProfileStorage().saveAsync(profile);
            }
        });
    }
}
