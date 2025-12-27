package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import me.lotiny.misty.api.event.PlayerScatterEvent;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PearlGiverScenario extends Scenario {

    @Autowired
    private static GameManager gameManager;

    @Override
    public String getName() {
        return "Pearl Giver";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.ENDER_PEARL)
                .name("&b" + getName())
                .lore(
                        "&7Player start with 3 ender pearl and when someone died",
                        "&7all alive players will receive 1 ender pearl."
                )
                .build();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerScatter(PlayerScatterEvent event) {
        Player player = event.getPlayer();

        player.getInventory().addItem(ItemStackUtils.of(XMaterial.ENDER_PEARL, 3));
    }

    @EventHandler
    public void handlePlayerDeath(PlayerDeathEvent event) {
        gameManager.getRegistry().getAlivePlayers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.getInventory().addItem(ItemStackUtils.of(XMaterial.ENDER_PEARL));
            }
        });
    }
}
