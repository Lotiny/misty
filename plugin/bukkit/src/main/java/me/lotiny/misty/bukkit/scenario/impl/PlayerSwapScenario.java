package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.Fairy;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import io.fairyproject.mc.scheduler.MCSchedulers;
import io.fairyproject.util.FastRandom;
import me.lotiny.misty.api.event.UHCMinuteEvent;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class PlayerSwapScenario extends Scenario {

    @Autowired
    private static GameManager gameManager;

    @Override
    public String getName() {
        return "Player Swap";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.END_PORTAL_FRAME)
                .name("&b" + getName())
                .lore(
                        "&7Every 5 minutes 2 random player will be swap the location."
                )
                .build();
    }

    @EventHandler
    public void handleMinute(UHCMinuteEvent event) {
        GameRegistry registry = gameManager.getRegistry();
        int minutes = event.getMinutes();

        if (registry.getPlayers().size() < 2) return;

        if (minutes % 5 == 0) {
            List<UUID> profiles = registry.getAlivePlayers().stream()
                    .filter(uuid -> Bukkit.getPlayer(uuid) != null)
                    .toList();

            if (profiles.size() < 2) return;

            FastRandom random = Fairy.random();
            UUID uuid1;
            UUID uuid2;

            do {
                uuid1 = profiles.get(random.nextInt(profiles.size()));
                uuid2 = profiles.get(random.nextInt(profiles.size()));
            } while (uuid1 == uuid2);

            Player player1 = Bukkit.getPlayer(uuid1);
            Player player2 = Bukkit.getPlayer(uuid2);

            if (player1 == null || player2 == null) return;

            Location location1 = player1.getLocation();
            Location location2 = player2.getLocation();

            MCSchedulers.getGlobalScheduler().schedule(() -> {
                player1.teleport(location2);
                player2.teleport(location1);
            });

            player1.sendMessage(Message.PLAYER_SWAP_PLAYER.toString()
                    .replace("<player>", player2.getName()));

            player2.sendMessage(Message.PLAYER_SWAP_PLAYER.toString()
                    .replace("<player>", player1.getName()));

            Utilities.broadcast(Message.PLAYER_SWAP_BROADCAST.toString()
                    .replace("<player1>", player1.getName())
                    .replace("<player2>", player2.getName()));
        }
    }
}
