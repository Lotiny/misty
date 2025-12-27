package me.lotiny.misty.bukkit.provider.menus.staff;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import io.fairyproject.bukkit.gui.Gui;
import io.fairyproject.bukkit.gui.pane.NormalPane;
import io.fairyproject.bukkit.gui.pane.PaginatedPane;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.bukkit.Permission;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.provider.menus.MenuItem;
import me.lotiny.misty.bukkit.provider.menus.MistyPaginatedMenu;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WhitelistMenu extends MistyPaginatedMenu {

    @Autowired
    private static GameManager gameManager;

    @Override
    public Component getTitle(Player player) {
        return Component.text("Whitelisted Players");
    }

    @Override
    public int getRows(Player player) {
        return 5;
    }

    @Override
    public List<MenuItem> getButtons(Player player, PaginatedPane pane, Gui gui) {
        List<MenuItem> buttons = new ArrayList<>();

        GameRegistry registry = gameManager.getRegistry();

        if (registry.getWhitelistPlayers().isEmpty()) {
            buttons.add(MenuItem.of(
                    ItemBuilder.of(XMaterial.BARRIER)
                            .name("&cNo players whitelisted")
                            .lore("&7Use &b/whitelist add <player> &7to add one.")
                            .build()
            ));
            return buttons;
        }

        for (String whitelisted : new ArrayList<>(registry.getWhitelistPlayers())) {
            buttons.add(MenuItem.of(
                    ItemBuilder.of(XMaterial.PLAYER_HEAD)
                            .name("&b" + whitelisted)
                            .skull(whitelisted)
                            .lore(
                                    " ",
                                    "&7Click to remove this",
                                    "&7player from whitelist.",
                                    " "
                            ).build(),
                    (clickedPlayer, clickType) -> {
                        if (!clickedPlayer.hasPermission(Permission.HOST_PERMISSION)) return;

                        registry.getWhitelistPlayers().remove(whitelisted);
                        PlayerUtils.playSound(clickedPlayer, XSound.BLOCK_ANVIL_BREAK, XSound.UI_BUTTON_CLICK);
                        clickedPlayer.sendMessage(Message.WHITELIST_REMOVE.toString()
                                .replace("<player>", whitelisted));

                        open(clickedPlayer);
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
