package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import me.lotiny.misty.api.event.UHCStartEvent;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

public class PermanightScenario extends Scenario {

    @Autowired
    private static GameManager gameManager;

    @Override
    public String getName() {
        return "Permanight";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.COAL_BLOCK)
                .name("&b" + getName())
                .lore(
                        "&7No day time!"
                )
                .build();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleStart(UHCStartEvent event) {
        setNightTime();
    }

    @Override
    public void onEnable() {
        if (gameManager.getRegistry().getState() == GameState.INGAME) {
            setNightTime();
        }
    }

    private void setNightTime() {
        World world = Bukkit.getWorld(gameManager.getRegistry().getUhcWorld());
        if (world != null) {
            world.setTime(16000);
            ReflectionUtils.get().setGameRule(world, "doDaylightCycle", false);
        }
    }
}
