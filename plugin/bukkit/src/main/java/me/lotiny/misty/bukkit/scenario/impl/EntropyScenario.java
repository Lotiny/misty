package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import me.lotiny.misty.api.event.UHCMinuteEvent;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.enums.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class EntropyScenario extends Scenario {

    @Autowired
    private static GameManager gameManager;

    @Override
    public String getName() {
        return "Entropy";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.EXPERIENCE_BOTTLE)
                .name("&b" + getName())
                .lore(
                        "&7Every 10 minutes every players will lose 1 level",
                        "&7if the player do not have any level they will died."
                )
                .build();
    }

    @EventHandler
    public void handleMinute(UHCMinuteEvent event) {
        int minutes = event.getMinutes();

        if (minutes % 10 == 0) {
            for (UUID uuid : gameManager.getRegistry().getAlivePlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    int levels = player.getLevel();

                    if (levels > 0) {
                        player.setLevel(levels - 1);
                        player.sendMessage(Message.ENTROPY_LEVEL.toString());
                    } else {
                        player.setHealth(0);
                        player.sendMessage(Message.ENTROPY_DEAD.toString());
                    }
                }
            }
        }
    }
}
