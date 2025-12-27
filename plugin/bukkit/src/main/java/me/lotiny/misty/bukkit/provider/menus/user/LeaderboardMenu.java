package me.lotiny.misty.bukkit.provider.menus.user;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.gui.Gui;
import io.fairyproject.bukkit.gui.pane.NormalPane;
import io.fairyproject.bukkit.gui.pane.PaginatedPane;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import io.fairyproject.util.CC;
import me.lotiny.misty.api.profile.stats.StatType;
import me.lotiny.misty.bukkit.manager.leaderboard.Leaderboard;
import me.lotiny.misty.bukkit.manager.leaderboard.LeaderboardManager;
import me.lotiny.misty.bukkit.provider.menus.MenuItem;
import me.lotiny.misty.bukkit.provider.menus.MistyPaginatedMenu;
import me.lotiny.misty.bukkit.storage.StorageRegistry;
import me.lotiny.misty.bukkit.utils.Utilities;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeaderboardMenu extends MistyPaginatedMenu {

    @Autowired
    private static LeaderboardManager leaderboardManager;
    @Autowired
    private static StorageRegistry storageRegistry;

    @Override
    public Component getTitle(Player player) {
        return Component.text("Leaderboard");
    }

    @Override
    public int getRows(Player player) {
        return 3;
    }

    @Override
    public List<MenuItem> getButtons(Player player, PaginatedPane pane, Gui gui) {
        List<MenuItem> buttons = new ArrayList<>();

        for (StatType statType : StatType.values()) {
            buttons.add(MenuItem.of(
                    ItemBuilder.of(statType.getMaterial())
                            .name("&b" + Utilities.getFormattedName(statType.name()))
                            .lore(buildLore(statType))
                            .build()
            ));
        }

        return buttons;
    }

    @Override
    public Map<Integer, MenuItem> getBorderButtons(Player player, NormalPane topPane, NormalPane bottomPane, Gui gui) {
        return Map.of(
                4, MenuItem.of(
                        ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                .skull(player)
                                .name("&eYour Stats")
                                .lore("&7Click to view your stats!")
                                .build()
                        , (clickedPlayer, clickType) -> {
                            new StatsMenu(storageRegistry.getProfile(clickedPlayer.getUniqueId())).open(clickedPlayer);
                            playClick(clickedPlayer);
                        }
                )
        );
    }

    private List<String> buildLore(StatType statType) {
        List<String> lore = new ArrayList<>();
        lore.add(CC.MENU_BAR);

        Leaderboard leaderboard = leaderboardManager.getLeaderboardMap().get(statType);
        if (leaderboard != null) {
            leaderboard.getPlayers().forEach((place, player) ->
                    lore.add("&9#" + place + " &e" + player.getName() + " &7- &b" + player.getValue())
            );
        } else {
            lore.add("&cNo data available.");
        }

        lore.add(CC.MENU_BAR);
        return lore;
    }
}
