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
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.command.MistyPresenceProvider;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.manager.leaderboard.Leaderboard;
import me.lotiny.misty.bukkit.manager.leaderboard.LeaderboardHologram;
import me.lotiny.misty.bukkit.storage.StorageRegistry;

@InjectableComponent
@RequiredArgsConstructor
@Command(value = {"uhchologram", "uhcholo", "uhcholograms"}, permissionNode = "misty.command.hologram")
@CommandPresence(MistyPresenceProvider.class)
public class HologramCommand extends AbstractCommand {

    private final StorageRegistry storageRegistry;

    @Override
    public void onHelp(CommandContext context) {
        context.sendMessage(
                MessageType.INFO,
                CC.CHAT_BAR,
                "&b/uhcholo create <leaderboard> &7- &fCreate a leaderboard hologram",
                "&b/uhcholo delete <leaderboard> &7- &fDelete a leaderboard hologram",
                CC.CHAT_BAR
        );
    }

    @Command("create")
    public void onCreate(BukkitCommandContext context, @Arg("leaderboard") Leaderboard leaderboard) {
        mustBePlayer(context, player -> {
            storageRegistry.getLeaderboardHologramStorage().getAsync(leaderboard.getStatType().getData())
                    .thenAccept(hologram -> {
                        if (hologram.getHologram() != null) {
                            hologram.move(player.getLocation());
                        }

                        hologram.spawn();
                    });

            player.sendMessage(Message.HOLOGRAM_CREATE.toString()
                    .replace("<type>", leaderboard.getDisplayName()));
        });
    }

    @Command("delete")
    public void onDelete(BukkitCommandContext context, @Arg("leaderboard") Leaderboard leaderboard) {
        mustBePlayer(context, player -> {
            LeaderboardHologram hologram = storageRegistry.getLeaderboardHologramStorage().get(leaderboard.getStatType().getData());
            storageRegistry.getLeaderboardHologramStorage().delete(hologram);
            hologram.remove();

            player.sendMessage(Message.HOLOGRAM_DELETE.toString()
                    .replace("<type>", leaderboard.getDisplayName()));
        });
    }
}
