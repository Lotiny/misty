package me.lotiny.misty.bukkit.provider.menus.staff.configuration;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import io.fairyproject.bukkit.gui.Gui;
import io.fairyproject.bukkit.gui.pane.NormalPane;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import me.lotiny.misty.api.game.ConfigType;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.bukkit.provider.menus.MenuItem;
import me.lotiny.misty.bukkit.provider.menus.MistyMenu;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.Utilities;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ConfigChangeMenu extends MistyMenu {

    @Autowired
    private static GameManager gameManager;

    private final ConfigType configType;
    private int currentAmount;

    public ConfigChangeMenu(ConfigType configType, int currentAmount) {
        this.configType = configType;
        this.currentAmount = currentAmount;
    }

    @Override
    public Component getTitle(Player player) {
        return Component.text("Config Editor");
    }

    @Override
    public int getRows(Player player) {
        return 3;
    }

    @Override
    public Map<Integer, MenuItem> getButtons(Player player, NormalPane pane, Gui gui) {
        Map<Integer, MenuItem> buttons = new HashMap<>();

        buttons.put(10, buildAdjustButton(-10, "&c-10"));
        buttons.put(11, buildAdjustButton(-5, "&c-5"));
        buttons.put(12, buildAdjustButton(-1, "&c-1"));

        buttons.put(13, buildInfoButton());

        buttons.put(14, buildAdjustButton(1, "&a+1"));
        buttons.put(15, buildAdjustButton(5, "&a+5"));
        buttons.put(16, buildAdjustButton(10, "&a+10"));

        buttons.put(26, MenuItem.of(
                ItemBuilder.of(XMaterial.EMERALD_BLOCK)
                        .name("&aConfirm & Save")
                        .build(),
                (clickedPlayer, clickType) -> {
                    gameManager.getGame().getSetting().setConfig(configType, currentAmount, clickedPlayer);
                    PlayerUtils.playSound(clickedPlayer, XSound.ENTITY_EXPERIENCE_ORB_PICKUP);
                    new ConfigMenu(gameManager.getGame().getSetting()).open(clickedPlayer);
                }
        ));

        return buttons;
    }

    private MenuItem buildAdjustButton(int amount, String displayName) {
        XMaterial material = amount > 0 ? XMaterial.GREEN_STAINED_GLASS_PANE : XMaterial.RED_STAINED_GLASS_PANE;

        return MenuItem.of(
                ItemBuilder.of(material)
                        .name(displayName)
                        .build(),
                (clickedPlayer, clickType) -> {
                    if (amount < 0 && currentAmount == 1) return;

                    currentAmount = Math.max(1, currentAmount + amount);
                    playClick(clickedPlayer);
                    open(clickedPlayer);
                }
        );
    }

    private MenuItem buildInfoButton() {
        return MenuItem.of(
                ItemBuilder.of(configType.getMaterial())
                        .name("&b" + Utilities.getFormattedName(configType.name()) + "&7: &f" + currentAmount)
                        .build()
        );
    }
}
