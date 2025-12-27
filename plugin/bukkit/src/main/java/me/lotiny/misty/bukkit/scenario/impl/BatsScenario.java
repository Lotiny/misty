package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.Fairy;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class BatsScenario extends Scenario {

    @Override
    public String getName() {
        return "Bats";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.BAT_SPAWN_EGG)
                .name("&b" + getName())
                .lore(
                        "&7Whenever you kill a bat there is a 95% chance",
                        "&7to drop 1 Golden Apple and a 5% chance to kill you."
                )
                .build();
    }

    @EventHandler
    public void handleEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Bat bat) {

            if (bat.getKiller() != null) {
                Player player = bat.getKiller();
                if (Fairy.random().nextInt(100) < 95) {
                    UHCUtils.dropItem(bat.getLocation(), XMaterial.GOLDEN_APPLE.parseItem());
                    player.sendMessage(Message.BATS_LUCKY.toString());
                } else {
                    bat.getKiller().setHealth(0);
                    player.sendMessage(Message.BATS_UNLUCKY.toString());
                }
            }
        }
    }
}
