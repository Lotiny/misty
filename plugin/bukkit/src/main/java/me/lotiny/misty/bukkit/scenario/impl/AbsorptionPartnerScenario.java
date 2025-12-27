package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.util.ConditionUtils;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.bukkit.scenario.annotations.IncompatibleWith;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.UUID;

@IncompatibleWith(AbsorptionlessScenario.class)
public class AbsorptionPartnerScenario extends Scenario {

    @Override
    public String getName() {
        return "Absorption Partner";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.GOLDEN_CARROT)
                .name("&b" + getName())
                .lore(
                        "&7Whenever you eat a golden apple, your teammate",
                        "&7will gain an absorption heart."
                )
                .build();
    }

    @EventHandler
    public void handlePlayerItemConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (XMaterial.GOLDEN_APPLE.isSimilar(item)) {
            Player player = event.getPlayer();
            Team team = UHCUtils.getTeam(player);
            if (team != null) {
                for (UUID uuid : team.getMembers(true)) {
                    Player member = Bukkit.getPlayer(uuid);
                    if (member != null) {
                        PotionEffect effect = XPotion.ABSORPTION.buildPotionEffect(3000, 1);
                        ConditionUtils.notNull(effect, "Failed to create Potion Effect");

                        member.addPotionEffect(effect);
                        member.sendMessage("&aYou have received an absorption heart from " + player.getName() + "!");
                    }
                }
            }
        }
    }
}
