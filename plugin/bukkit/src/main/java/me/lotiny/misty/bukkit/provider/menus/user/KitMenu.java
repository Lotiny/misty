package me.lotiny.misty.bukkit.provider.menus.user;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.gui.Gui;
import io.fairyproject.bukkit.gui.pane.NormalPane;
import io.fairyproject.bukkit.gui.pane.PaginatedPane;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import io.fairyproject.util.CC;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.kit.Kit;
import me.lotiny.misty.bukkit.kit.KitManager;
import me.lotiny.misty.bukkit.provider.menus.MenuItem;
import me.lotiny.misty.bukkit.provider.menus.MistyPaginatedMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KitMenu extends MistyPaginatedMenu {

    @Autowired
    private static KitManager kitManager;

    @Override
    public Component getTitle(Player player) {
        return Component.text("Kits");
    }

    @Override
    public int getRows(Player player) {
        return 5;
    }

    @Override
    public List<MenuItem> getButtons(Player player, PaginatedPane pane, Gui gui) {
        List<MenuItem> buttons = new ArrayList<>();
        kitManager.getKits().forEach((id, kit) -> {
            boolean isDefault = Config.getMainConfig().getKit().getDefaultKit() == id;

            buttons.add(MenuItem.of(
                    ItemBuilder.of(XMaterial.PAPER)
                            .name("&e&lKit #" + id)
                            .lore(
                                    CC.MENU_BAR,
                                    "&eKit ID: &f" + id,
                                    "&eDefault Kit: &f" + isDefault,
                                    " ",
                                    "&7Click to preview the kit.",
                                    "&7Shift-click to delete the kit.",
                                    CC.MENU_BAR
                            )
                            .build()
                    , (clickedPlayer, clickType) -> {
                        if (clickType.isShiftClick()) {
                            kitManager.getKits().remove(id);
                            clickedPlayer.sendMessage(CC.translate("&cRemoved Kit #" + id));
                        } else {
                            clickedPlayer.closeInventory();
                            clickedPlayer.getInventory().setArmorContents(kit.getArmors());
                            clickedPlayer.getInventory().setContents(kit.getItems());
                            clickedPlayer.sendMessage(CC.translate("&aSet your items to Kit #" + id));
                        }
                    }
            ));
        });

        return buttons;
    }

    @Override
    public Map<Integer, MenuItem> getBorderButtons(Player player, NormalPane topPane, NormalPane bottomPane, Gui gui) {
        return Map.of(
                39, MenuItem.of(
                        ItemBuilder.of(XMaterial.EMERALD_BLOCK)
                                .name("&aCreate Kit")
                                .build()
                        , (clickedPlayer, clickPlayer) -> {
                            int id = kitManager.findAvailableId(kitManager.getKits());
                            Kit kit = new Kit(clickedPlayer.getInventory().getArmorContents(), clickedPlayer.getInventory().getContents());
                            kitManager.getKits().put(id, kit);
                            clickedPlayer.sendMessage(CC.translate("&aSuccessfully created a new Kit #" + id));
                        }
                )
        );
    }
}
