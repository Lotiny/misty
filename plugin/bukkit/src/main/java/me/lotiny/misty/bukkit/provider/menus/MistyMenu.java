package me.lotiny.misty.bukkit.provider.menus;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import io.fairyproject.bukkit.gui.Gui;
import io.fairyproject.bukkit.gui.GuiFactory;
import io.fairyproject.bukkit.gui.pane.NormalPane;
import io.fairyproject.bukkit.gui.pane.Pane;
import io.fairyproject.bukkit.gui.slot.GuiSlot;
import io.fairyproject.container.Autowired;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Map;

public abstract class MistyMenu {

    @Autowired
    private static GuiFactory factory;

    public abstract Component getTitle(Player player);

    public abstract int getRows(Player player);

    public abstract Map<Integer, MenuItem> getButtons(Player player, NormalPane pane, Gui gui);

    public boolean isFilled(Player player) {
        return true;
    }

    public void open(Player player) {
        Gui gui = factory.create(getTitle(player));
        NormalPane pane = Pane.normal(getRows(player));

        getButtons(player, pane, gui).forEach((slot, menuItem) -> pane.setSlot(slot, menuItem.build()));

        if (isFilled(player)) {
            pane.fillEmptySlots(GuiSlot.of(
                    XMaterial.GRAY_STAINED_GLASS_PANE, " "
            ));
        }

        gui.addPane(pane);

        if (gui.isOpening()) {
            player.closeInventory();
        }
        gui.open(player);
    }

    public void playClick(Player player) {
        PlayerUtils.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON, XSound.UI_BUTTON_CLICK);
    }
}
