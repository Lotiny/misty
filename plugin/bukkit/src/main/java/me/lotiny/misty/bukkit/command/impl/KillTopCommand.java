package me.lotiny.misty.bukkit.command.impl;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.util.CC;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.profile.stats.Stats;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.KeyEx;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.Bukkit;

import java.util.*;

@InjectableComponent
@RequiredArgsConstructor
@Command({"kt", "killtop", "tk", "topkill"})
public class KillTopCommand extends AbstractCommand {

    private final GameManager gameManager;

    @Command("#")
    public void onCommand(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            GameState gameState = gameManager.getRegistry().getState();

            if (gameState == GameState.LOBBY || gameState == GameState.SCATTERING) {
                player.sendMessage(Message.WRONG_STATE.toString());
                return;
            }

            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.translate("&eTop 10 Kills: "));
            player.sendMessage(" ");

            Collection<UUID> uuids = gameManager.getRegistry().getPlayers().keySet();
            List<UUID> topTen = uuids.stream()
                    .map(uuid -> new AbstractMap.SimpleEntry<>(uuid, this.getGameKills(uuid)))
                    .filter(entry -> entry.getValue() > 0)
                    .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                    .limit(10)
                    .map(Map.Entry::getKey)
                    .toList();

            for (UUID uuid : topTen) {
                int kills = this.getGameKills(uuid);
                String name = Bukkit.getOfflinePlayer(uuid).getName();
                String message = "&7- " + (UHCUtils.isAlive(uuid) ? "&a" : "&c") +
                        name + "&7: &b" + kills + " Kills";
                player.sendMessage(CC.translate(message));
            }

            player.sendMessage(CC.CHAT_BAR);
        });
    }

    private int getGameKills(UUID uuid) {
        return Metadata.provideForPlayer(uuid).getOrDefault(KeyEx.GAME_KILLS_KEY, new Stats()).getAmount();
    }
}
