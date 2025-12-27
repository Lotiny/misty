package me.lotiny.misty.bukkit.provider.menus.staff.configuration;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import io.fairyproject.bootstrap.bukkit.BukkitPlugin;
import io.fairyproject.bukkit.gui.Gui;
import io.fairyproject.bukkit.gui.pane.NormalPane;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import io.fairyproject.util.CC;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameSetting;
import me.lotiny.misty.bukkit.Permission;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.impl.UHCConfig;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.provider.menus.MenuItem;
import me.lotiny.misty.bukkit.provider.menus.MistyMenu;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import net.kyori.adventure.text.Component;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ConfigEditorMenu extends MistyMenu {

    private static final int SLOT_RENAME = 10;
    private static final int SLOT_DEFAULT = 13;
    private static final int SLOT_DELETE = 16;
    @Autowired
    private static GameManager gameManager;
    private final GameSetting setting;

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

        buttons.put(SLOT_RENAME, buildRenameButton());
        buttons.put(SLOT_DEFAULT, buildDefaultButton());
        buttons.put(SLOT_DELETE, buildDeleteButton());

        return buttons;
    }

    private MenuItem buildRenameButton() {
        return MenuItem.of(
                ItemBuilder.of(XMaterial.NAME_TAG)
                        .name("&6Rename")
                        .lore(
                                " ",
                                "&7Click to rename this config.",
                                " "
                        )
                        .build(),
                (player, clickType) -> {
                    if (!player.hasPermission(Permission.HOST_PERMISSION)) return;
                    playClick(player);
                    player.closeInventory();

                    new AnvilGUI.Builder()
                            .onClick((slot, state) -> Collections.singletonList(AnvilGUI.ResponseAction.close()))
                            .onClose(state -> handleRename(player, state.getText()))
                            .text("Enter Config Name")
                            .plugin(BukkitPlugin.INSTANCE)
                            .open(player);
                }
        );
    }

    private void handleRename(Player player, String name) {
        if (name == null || name.isEmpty()) return;

        setting.setConfigName(name);

        UHCConfig config = Config.getUhcConfig();
        config.getGameConfig()
                .get(setting.getConfigId())
                .setName(name);
        config.save();

        PlayerUtils.playSound(player, XSound.ENTITY_EXPERIENCE_ORB_PICKUP);
        player.sendMessage(Message.CONFIG_EDIT_NAME.toString()
                .replace("<name>", name));
    }

    private MenuItem buildDefaultButton() {
        return MenuItem.of(
                ItemBuilder.of(XMaterial.LEVER)
                        .name("&dDefault")
                        .lore(
                                " ",
                                "&7If enabled this config will be",
                                "&7use as default config.",
                                " ",
                                "&fCurrently&7: " + (setting.isDef() ? "&aEnabled" : "&cDisabled"),
                                " "
                        ).build(),
                (player, clickType) -> {
                    if (!player.hasPermission(Permission.HOST_PERMISSION)) return;
                    if (setting.isDef()) {
                        player.sendMessage(CC.RED + "You have to set other configuration to the default config, and this will no longer be the default config.");
                        return;
                    }

                    for (GameSetting allConfig : gameManager.getGameSettingMap().values()) {
                        if (allConfig.isDef()) {
                            allConfig.setDef(false);
                            gameManager.saveGame(allConfig);
                        }
                    }

                    setting.setDef(true);
                    gameManager.saveGame(setting);

                    playClick(player);
                    open(player);
                }
        );
    }

    private MenuItem buildDeleteButton() {
        return MenuItem.of(
                ItemBuilder.of(XMaterial.REDSTONE)
                        .name("&cDelete Config")
                        .lore(
                                " ",
                                "&7Shift-Click to delete this config.",
                                " "
                        )
                        .build(),
                (player, clickType) -> {
                    if (!player.hasPermission(Permission.HOST_PERMISSION) || !clickType.isShiftClick()) return;
                    if (gameManager.getGameSettingMap().remove(setting.getConfigId()) != null) {
                        Config.getUhcConfig().getGameConfig().remove(setting.getConfigId());
                        playClick(player);
                        player.closeInventory();
                    }
                }
        );
    }
}
