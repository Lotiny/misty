package me.lotiny.misty.bukkit.provider.menus.staff;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.gui.Gui;
import io.fairyproject.bukkit.gui.pane.NormalPane;
import io.fairyproject.bukkit.gui.pane.PaginatedPane;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import me.lotiny.misty.api.game.ConfigType;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.bukkit.Permission;
import me.lotiny.misty.bukkit.manager.border.BorderManager;
import me.lotiny.misty.bukkit.provider.menus.MenuItem;
import me.lotiny.misty.bukkit.provider.menus.MistyPaginatedMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BorderSizeMenu extends MistyPaginatedMenu {

    @Autowired
    private static GameManager gameManager;
    @Autowired
    private static BorderManager borderManager;

    @Override
    public Component getTitle(Player player) {
        return Component.text("Border Size");
    }

    @Override
    public int getRows(Player player) {
        return 3;
    }

    @Override
    public List<MenuItem> getButtons(Player player, PaginatedPane pane, Gui gui) {
        List<MenuItem> buttons = new ArrayList<>();
        int currentSize = gameManager.getGame().getSetting().getBorderSize();

        for (int size : borderManager.getAllowedBorderSizes()) {
            boolean isActive = size == currentSize;

            buttons.add(MenuItem.of(
                    ItemBuilder.of(XMaterial.PAPER)
                            .name("&b" + size + "x" + size)
                            .lore(
                                    " ",
                                    isActive ? "&e» &aSelected" : "&7Click to set border size",
                                    " "
                            ).build(),
                    (clickedPlayer, clickType) -> {
                        if (!clickedPlayer.hasPermission(Permission.HOST_PERMISSION)) return;

                        if (!isActive) {
                            gameManager.getGame().getSetting().setConfig(ConfigType.BORDER_SIZE, size, clickedPlayer);
                        }

                        playClick(clickedPlayer);
                        open(clickedPlayer, pane.getPage());
                    }
            ));
        }

        return buttons;
    }

    @Override
    public Map<Integer, MenuItem> getBorderButtons(Player player, NormalPane topPane, NormalPane bottomPane, Gui gui) {
        return Map.of();
    }
}

