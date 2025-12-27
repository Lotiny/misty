package me.lotiny.misty.bukkit.provider.menus.staff;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.gui.Gui;
import io.fairyproject.bukkit.gui.pane.NormalPane;
import io.fairyproject.bukkit.gui.pane.PaginatedPane;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import io.fairyproject.util.CC;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.bukkit.Permission;
import me.lotiny.misty.bukkit.provider.menus.MenuItem;
import me.lotiny.misty.bukkit.provider.menus.MistyPaginatedMenu;
import me.lotiny.misty.bukkit.utils.cooldown.CombatLoggerCooldown;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DisqualifyMenu extends MistyPaginatedMenu {

    @Autowired
    private static GameManager gameManager;

    @Override
    public Component getTitle(Player player) {
        return Component.text("Disqualify Players");
    }

    @Override
    public int getRows(Player player) {
        return 5;
    }

    @Override
    public List<MenuItem> getButtons(Player player, PaginatedPane pane, Gui gui) {
        List<MenuItem> buttons = new ArrayList<>();

        gameManager.getRegistry().getCombatLoggers().values().stream()
                .filter(Objects::nonNull)
                .forEach(logger -> buttons.add(MenuItem.of(
                        ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                .name("&b" + logger.getPlayerName())
                                .skull(logger.getPlayerName())
                                .lore(
                                        CC.MENU_BAR,
                                        "&eDuration left &c" + ((CombatLoggerCooldown) gameManager.getCombatLoggerCooldown()).getCooldownTimer(logger),
                                        " ",
                                        "&7Shift-Click to disqualify!",
                                        CC.MENU_BAR
                                ).build(),
                        (clickedPlayer, clickType) -> {
                            if (!clickedPlayer.hasPermission(Permission.HOST_PERMISSION)) return;

                            if (clickType.isShiftClick()) {
                                logger.remove();
                                clickedPlayer.sendMessage(CC.translate("&cYou have disqualified &4" + logger.getPlayerName()));
                                playClick(clickedPlayer);
                                open(clickedPlayer, pane.getPage());
                            }
                        }
                )));

        if (buttons.isEmpty()) {
            buttons.add(MenuItem.of(
                    ItemBuilder.of(XMaterial.BARRIER)
                            .name("&cNo combat loggers found")
                            .lore("&7Currently, no players are logged out in combat.")
                            .build()
            ));
        }

        return buttons;
    }

    @Override
    public Map<Integer, MenuItem> getBorderButtons(Player player, NormalPane topPane, NormalPane bottomPane, Gui gui) {
        return Map.of();
    }
}
