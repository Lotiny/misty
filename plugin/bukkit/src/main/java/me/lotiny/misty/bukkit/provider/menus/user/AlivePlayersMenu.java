package me.lotiny.misty.bukkit.provider.menus.user;

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
import me.lotiny.misty.bukkit.provider.menus.staff.InspectMenu;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlivePlayersMenu extends MistyPaginatedMenu {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.0");
    @Autowired
    private static GameManager gameManager;

    @Override
    public Component getTitle(Player player) {
        return Component.text("Alive Players");
    }

    @Override
    public int getRows(Player player) {
        return 4;
    }

    @Override
    public List<MenuItem> getButtons(Player viewer, PaginatedPane pane, Gui gui) {
        List<MenuItem> buttons = new ArrayList<>();

        gameManager.getRegistry().getAlivePlayers().forEach(uuid -> {
            Player online = Bukkit.getPlayer(uuid);
            if (online == null) return;

            buttons.add(buildPlayerButton(online));
        });

        return buttons;
    }

    private MenuItem buildPlayerButton(Player target) {
        return MenuItem.of(
                ItemBuilder.of(XMaterial.PLAYER_HEAD)
                        .name("&a" + target.getName())
                        .skull(target.getName())
                        .lore(
                                CC.MENU_BAR,
                                "&eHealth &a" + DECIMAL_FORMAT.format(target.getHealth()) + "/" + DECIMAL_FORMAT.format(PlayerUtils.getMaxHealth(target)),
                                "&eFood Level &a" + target.getFoodLevel() + "/20",
                                "&eLevel &a" + target.getLevel(),
                                " ",
                                "&7&oLeft Click to teleport!",
                                CC.MENU_BAR
                        )
                        .build(),
                (player, clickType) -> {
                    if (clickType.isLeftClick()) {
                        player.teleport(target);
                        player.closeInventory();
                    } else if (clickType.isRightClick() && player.hasPermission(Permission.HOST_PERMISSION)) {
                        new InspectMenu(target).open(player);
                    }
                }
        );
    }

    @Override
    public Map<Integer, MenuItem> getBorderButtons(Player player, NormalPane topPane, NormalPane bottomPane, Gui gui) {
        return Map.of();
    }
}
