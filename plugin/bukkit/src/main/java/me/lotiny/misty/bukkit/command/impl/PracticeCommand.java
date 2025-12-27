package me.lotiny.misty.bukkit.command.impl;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.bukkit.util.BukkitPos;
import io.fairyproject.command.MessageType;
import io.fairyproject.command.annotation.Arg;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.command.annotation.CommandPresence;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.mc.util.Position;
import io.fairyproject.util.CC;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.command.MistyPresenceProvider;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.impl.PracticeConfig;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.manager.PracticeManager;

@InjectableComponent
@RequiredArgsConstructor
@Command({"practice", "prac"})
@CommandPresence(MistyPresenceProvider.class)
public class PracticeCommand extends AbstractCommand {

    private final PracticeManager practiceManager;

    @Command("#")
    public void onCommand(BukkitCommandContext commandContext) {
        BukkitCommandContext context = commandContext.as(BukkitCommandContext.class);
        mustBePlayer(context, player -> {
            if (practiceManager.isInPractice(player)) {
                practiceManager.leave(player);
            } else {
                practiceManager.join(player);
            }
        });
    }

    @Command(value = "help", permissionNode = "misty.command.practice")
    public void onHelp(BukkitCommandContext context) {
        context.sendMessage(
                MessageType.INFO,
                CC.CHAT_BAR,
                "&b/practice toggle &7- &fToggle enable/disable practice arena",
                "&b/practice help &7- &fShow this page",
                "&b/practice setmax <number> &7- &fSet practice's max slot",
                "&b/practice setkit &7- &fSet practice's kit",
                "&b/practice setlocation &7- &fSet practice's location",
                CC.CHAT_BAR
        );
    }

    @Command(value = "setlocation", permissionNode = "misty.command.practice")
    public void onSetLocation(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            Position position = BukkitPos.toMCPos(player.getLocation());
            Config.getPracticeConfig().setLocation(BukkitPos.toBukkitLocation(position));
            player.sendMessage(Message.PRACTICE_SET_LOCATION.toString());
        });
    }

    @Command(value = "setkit", permissionNode = "misty.command.practice")
    public void onSetKit(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            PracticeConfig config = Config.getPracticeConfig();
            config.getKit().setArmors(player.getInventory().getArmorContents());
            config.getKit().setItems(player.getInventory().getContents());
            config.save();

            player.sendMessage(Message.PRACTICE_SET_KIT.toString());
        });
    }

    @Command(value = "setmax", permissionNode = "misty.command.practice")
    public void onSetMax(BukkitCommandContext context, @Arg("amount") int amount) {
        practiceManager.setMaxPlayers(amount);
        context.sendMessage(MessageType.INFO, Message.PRACTICE_SET_MAX_PLAYERS.toString()
                .replace("<amount>", String.valueOf(amount)));
    }

    @Command(value = "toggle", permissionNode = "misty.command.practice")
    public void onToggle(BukkitCommandContext context) {
        practiceManager.setOpened(!practiceManager.isOpened(), context.getSender());
    }
}
