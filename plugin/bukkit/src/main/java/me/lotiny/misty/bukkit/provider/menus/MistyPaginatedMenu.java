package me.lotiny.misty.bukkit.provider.menus;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import io.fairyproject.bukkit.gui.Gui;
import io.fairyproject.bukkit.gui.GuiFactory;
import io.fairyproject.bukkit.gui.pane.NormalPane;
import io.fairyproject.bukkit.gui.pane.PaginatedPane;
import io.fairyproject.bukkit.gui.pane.Pane;
import io.fairyproject.bukkit.gui.slot.GuiSlot;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public abstract class MistyPaginatedMenu {

    @Autowired
    private static GuiFactory guiFactory;

    public abstract Component getTitle(Player player);

    public abstract int getRows(Player player);

    public abstract List<MenuItem> getButtons(Player player, PaginatedPane pane, Gui gui);

    public abstract Map<Integer, MenuItem> getBorderButtons(Player player, NormalPane topPane, NormalPane bottomPane, Gui gui);

    public boolean isFilled(Player player) {
        return true;
    }

    public void open(Player player, int page) {
        Gui gui = guiFactory.create(getTitle(player));
        int rows = getRows(player);

        NormalPane topPane = Pane.normal(0, 0, 9, 1);
        NormalPane bottomPane = Pane.normal(0, rows - 1, 9, 1);
        NormalPane leftPane = Pane.normal(0, 1, 1, rows - 2);
        NormalPane rightPane = Pane.normal(8, 1, 1, rows - 2);
        PaginatedPane centerPane = Pane.paginated(1, 1, 7, rows - 2);

        getButtons(player, centerPane, gui).forEach(menuItem -> centerPane.addSlot(menuItem.build()));

        getBorderButtons(player, topPane, bottomPane, gui).forEach((index, menuItem) -> {
            if (index < 9) {
                topPane.setSlot(index, menuItem.build());
            } else {
                bottomPane.setSlot(index - 9 * (rows - 1), menuItem.build());
            }
        });

        centerPane.setPage(Math.min(centerPane.getMaxPage(), page));

        if (centerPane.getMaxPage() > 1) {
            GuiSlot prev = GuiSlot.previousPage(centerPane, ItemBuilder.of(XMaterial.ARROW)
                    .name("&aPrevious Page")
                    .lore(" ", "&7Click to go to previous page.", " ")
                    .build());
            GuiSlot next = GuiSlot.nextPage(centerPane, ItemBuilder.of(XMaterial.ARROW)
                    .name("&aNext Page")
                    .lore(" ", "&7Click to go to next page.", " ")
                    .build());

            topPane.setSlot(0, prev);
            topPane.setSlot(8, next);
        }

        if (isFilled(player)) {
            GuiSlot filler = GuiSlot.of(XMaterial.GRAY_STAINED_GLASS_PANE, " ");
            topPane.fillEmptySlots(filler);
            bottomPane.fillEmptySlots(filler);
            leftPane.fillEmptySlots(filler);
            rightPane.fillEmptySlots(filler);
        }

        gui.addPane(topPane);
        gui.addPane(bottomPane);
        gui.addPane(leftPane);
        gui.addPane(rightPane);
        gui.addPane(centerPane);

        if (gui.isOpening()) {
            player.closeInventory();
        }
        gui.open(player);
    }

    public void open(Player player) {
        open(player, 0);
    }

    public void playClick(Player player) {
        PlayerUtils.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON, XSound.UI_BUTTON_CLICK);
    }
}
