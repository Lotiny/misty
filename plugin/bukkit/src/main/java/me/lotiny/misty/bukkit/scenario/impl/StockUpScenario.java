package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class StockUpScenario extends Scenario {

    @Autowired
    private static GameManager gameManager;

    @Override
    public String getName() {
        return "Stock Up";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.GOLDEN_PICKAXE)
                .name("&b" + getName())
                .lore(
                        "&7When player died other alive players will",
                        "&7receive 1 full heart."
                )
                .build();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        for (UUID uuid : gameManager.getRegistry().getAlivePlayers()) {
            Player online = Bukkit.getPlayer(uuid);
            if (online != null && online != player) {
                PlayerUtils.setMaxHealth(online, PlayerUtils.getMaxHealth(online) + 2);
            }
        }
    }
}
