package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.metadata.MetadataKey;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.scenario.annotations.Required;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@Required(TimebombScenario.class)
public class SafelootScenario extends Scenario {

    public static MetadataKey<Team> CHEST_KEY = MetadataKey.create("misty:safeloot-chest-key", Team.class);

    @Override
    public String getName() {
        return "Safeloot";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.LEVER)
                .name("&b" + getName())
                .lore(
                        "&7The Timebomb chest will be locked to the killer."
                )
                .build();
    }

    @EventHandler
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block == null || !XBlock.isSimilar(block, XMaterial.CHEST)) return;

            Player player = event.getPlayer();
            Metadata.provideForBlock(block).ifPresent(CHEST_KEY, team -> {
                if (UHCUtils.getTeam(player) != team) {
                    event.setCancelled(true);
                    player.sendMessage(Message.SAFELOOT_LOCKED.toString());
                }
            });
        }
    }
}
