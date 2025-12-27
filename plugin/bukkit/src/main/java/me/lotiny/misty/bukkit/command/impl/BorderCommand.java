package me.lotiny.misty.bukkit.command.impl;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.command.annotation.Arg;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameSetting;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.manager.border.BorderManager;
import me.lotiny.misty.bukkit.task.BorderTask;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@InjectableComponent
@RequiredArgsConstructor
@Command(value = {"border", "uhcborder"}, permissionNode = "misty.command.border")
public class BorderCommand extends AbstractCommand {

    private final GameManager gameManager;
    private final BorderManager borderManager;

    private Set<Integer> allowedSizes;

    @PostInitialize
    public void onPostInit() {
        this.allowedSizes = Arrays.stream(borderManager.getAllowedBorderSizes())
                .boxed()
                .collect(Collectors.toSet());
    }

    @Command("#")
    public void onCommand(BukkitCommandContext context, @Arg("size") int size) {
        mustBePlayer(context, player -> {
            GameRegistry registry = gameManager.getRegistry();
            GameSetting setting = gameManager.getGame().getSetting();

            if (!isSizeValid(size, setting.getBorderSize())) {
                player.sendMessage(Message.BORDER_FORCE_SHRINK_INVALID.toString());
                return;
            }

            BorderTask borderTask = (BorderTask) registry.getBorderTask();
            int shrinkTime = registry.isFirstShrunk() ? 11 : (setting.getFirstShrink() * 60) - 11;
            borderTask.setSeconds(shrinkTime);
            borderTask.setForceShrink(true);
            borderTask.setSize(size);

            player.sendMessage(Message.BORDER_FORCE_SHRINK_SHRUNK.toString()
                    .replace("<size>", String.valueOf(size)));
        });
    }

    private boolean isSizeValid(int size, int currentBorderSize) {
        return size < currentBorderSize && allowedSizes.contains(size);
    }
}
