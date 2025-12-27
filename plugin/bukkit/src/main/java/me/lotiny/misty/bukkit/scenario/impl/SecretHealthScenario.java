package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.event.PlayerScatterEvent;
import me.lotiny.misty.api.scenario.Scenario;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class SecretHealthScenario extends Scenario {

    @Override
    public String getName() {
        return "Secret Health";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.TRIPWIRE_HOOK)
                .name("&b" + getName())
                .lore(
                        "&7You cannot see other player health."
                )
                .build();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerScatter(PlayerScatterEvent event) {
        Player player = event.getPlayer();

        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(DisplaySlot.BELOW_NAME);
        if (objective == null) return;

        objective.unregister();
    }
}
