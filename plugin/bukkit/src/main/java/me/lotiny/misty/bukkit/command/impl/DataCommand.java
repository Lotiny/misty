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
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.api.profile.stats.StatType;
import me.lotiny.misty.api.profile.stats.Stats;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.command.MistyPresenceProvider;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.storage.StorageRegistry;
import me.lotiny.misty.bukkit.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@InjectableComponent
@RequiredArgsConstructor
@Command(value = "data", permissionNode = "misty.command.data")
@CommandPresence(MistyPresenceProvider.class)
public class DataCommand extends AbstractCommand {

    private final StorageRegistry storageRegistry;

    @Override
    public void onHelp(CommandContext context) {
        context.sendMessage(
                MessageType.INFO,
                CC.CHAT_BAR,
                "&b/data wipe &7- &fReset all players data",
                "&b/data reset <player> &7- &fReset player data",
                "&b/data add <player> <data> <amount> &7- &fAdd player data",
                "&b/data remove <player> <data> <amount> &7- &fRemove player data",
                "&b/data set <player> <data> <amount> &7- &fSet player data",
                CC.CHAT_BAR
        );
    }

    @Command("wipe")
    public void onWipe(BukkitCommandContext context) {
        storageRegistry.getProfileStorage().deleteAll();
        context.sendMessage(MessageType.INFO, "All player data has been dropped.");
    }

    @Command("reset")
    public void onReset(BukkitCommandContext context, @Arg("target") Profile profile) {
        storageRegistry.getProfileStorage().delete(profile);
        context.sendMessage(MessageType.INFO, Message.DATA_RESET.toString()
                .replace("<player>", profile.getName()));
    }

    @Command("add")
    public void onAdd(BukkitCommandContext context, @Arg("target") Profile profile, @Arg("statType") StatType data, @Arg("amount") int amount) {
        Stats stats = profile.getStats(data);
        stats.increase(amount);
        context.sendMessage(MessageType.INFO, Message.DATA_INCREASED.toString()
                .replace("<player>", profile.getName())
                .replace("<data>", Utilities.getFormattedName(data.name()))
                .replace("<amount>", String.valueOf(amount)));

        Player target = Bukkit.getPlayer(profile.getUniqueId());
        if (target == null) {
            storageRegistry.getProfileStorage().saveAsync(profile);
        }
    }

    @Command("remove")
    public void onRemove(BukkitCommandContext context, @Arg("target") Profile profile, @Arg("statType") StatType data, @Arg("amount") int amount) {
        Stats stats = profile.getStats(data);
        stats.decrease(amount);
        context.sendMessage(MessageType.INFO, Message.DATA_DECREASED.toString()
                .replace("<player>", profile.getName())
                .replace("<data>", Utilities.getFormattedName(data.name()))
                .replace("<amount>", String.valueOf(amount)));

        Player target = Bukkit.getPlayer(profile.getUniqueId());
        if (target == null) {
            storageRegistry.getProfileStorage().saveAsync(profile);
        }
    }

    @Command("set")
    public void onSet(BukkitCommandContext context, @Arg("target") Profile profile, @Arg("statType") StatType data, @Arg("amount") int amount) {
        Stats stats = profile.getStats(data);
        stats.setAmount(amount);
        context.sendMessage(MessageType.INFO, Message.DATA_SET.toString()
                .replace("<player>", profile.getName())
                .replace("<data>", Utilities.getFormattedName(data.name()))
                .replace("<amount>", String.valueOf(amount)));

        Player target = Bukkit.getPlayer(profile.getUniqueId());
        if (target == null) {
            storageRegistry.getProfileStorage().saveAsync(profile);
        }
    }
}
