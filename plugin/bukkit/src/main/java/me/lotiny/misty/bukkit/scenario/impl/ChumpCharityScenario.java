package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import me.lotiny.misty.api.event.UHCMinuteEvent;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ChumpCharityScenario extends Scenario {

    @Autowired
    private static GameManager gameManager;

    @Override
    public String getName() {
        return "Chump Charity";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.OAK_LEAVES)
                .name("&b" + getName())
                .lore(
                        "&7Every 10 minutes the player with lowest",
                        "&7health will be healed."
                )
                .build();
    }

    @EventHandler
    public void handleMinute(UHCMinuteEvent event) {
        int minutes = event.getMinutes();

        if (minutes % 10 == 0) {
            Player lowest = null;

            for (UUID uuid : gameManager.getRegistry().getAlivePlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null && (lowest == null || player.getHealth() < lowest.getHealth())) {
                    lowest = player;
                }
            }

            if (lowest != null) {
                lowest.setHealth(PlayerUtils.getMaxHealth(lowest));
                lowest.sendMessage(Message.CHUMP_CHARITY_PLAYER.toString());

                Utilities.broadcast(Message.CHUMP_CHARITY_BROADCAST.toString()
                        .replace("<player>", lowest.getName()));
            }
        }
    }
}
