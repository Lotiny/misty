package me.lotiny.misty.bukkit.provider.menus.staff;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import io.fairyproject.bootstrap.bukkit.BukkitPlugin;
import io.fairyproject.bukkit.gui.Gui;
import io.fairyproject.bukkit.gui.pane.NormalPane;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.bukkit.Permission;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.manager.PracticeManager;
import me.lotiny.misty.bukkit.manager.WorldManager;
import me.lotiny.misty.bukkit.provider.menus.MenuItem;
import me.lotiny.misty.bukkit.provider.menus.MistyMenu;
import me.lotiny.misty.bukkit.provider.menus.staff.configuration.ConfigMenu;
import me.lotiny.misty.bukkit.provider.menus.staff.configuration.SaveConfigsMenu;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.Utilities;
import net.kyori.adventure.text.Component;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsMenu extends MistyMenu {

    private static final int SLOT_WHITELIST_ADD = 12;
    private static final int SLOT_WHITELIST_TOGGLE = 13;
    private static final int SLOT_WHITELIST_VIEW = 14;
    private static final int SLOT_PRACTICE = 20;
    private static final int SLOT_WORLD_EDITOR = 22;
    private static final int SLOT_SAVE_CONFIGS = 24;
    private static final int SLOT_CONFIG = 30;
    private static final int SLOT_SCENARIOS = 32;
    @Autowired
    private static GameManager gameManager;
    @Autowired
    private static PracticeManager practiceManager;
    @Autowired
    private static WorldManager worldManager;
    @Autowired
    private static ScenarioManager scenarioManager;

    @Override
    public Component getTitle(Player player) {
        return Component.text("Settings");
    }

    @Override
    public int getRows(Player player) {
        return 5;
    }

    @Override
    public Map<Integer, MenuItem> getButtons(Player player, NormalPane pane, Gui gui) {
        Map<Integer, MenuItem> buttons = new HashMap<>();

        buttons.put(SLOT_WHITELIST_ADD, buildWhitelistAddButton());
        buttons.put(SLOT_WHITELIST_TOGGLE, buildWhitelistToggleButton());
        buttons.put(SLOT_WHITELIST_VIEW, buildWhitelistedPlayerButton());
        buttons.put(SLOT_PRACTICE, buildPracticeButton());
        buttons.put(SLOT_WORLD_EDITOR, buildWorldEditorButton());
        buttons.put(SLOT_SAVE_CONFIGS, buildSaveConfigsButton());
        buttons.put(SLOT_CONFIG, buildConfigButton());
        buttons.put(SLOT_SCENARIOS, buildScenariosButton());

        return buttons;
    }

    private MenuItem buildWhitelistAddButton() {
        return MenuItem.of(
                ItemBuilder.of(XMaterial.WRITABLE_BOOK)
                        .name("&2Whitelist Add")
                        .lore(
                                " ",
                                "&7Click to add player to whitelist",
                                " "
                        ).build(),
                (clickedPlayer, clickType) -> {
                    if (!clickedPlayer.hasPermission(Permission.HOST_PERMISSION)) return;
                    playClick(clickedPlayer);
                    clickedPlayer.closeInventory();

                    new AnvilGUI.Builder()
                            .onClick((slot, stateSnapshot) -> {
                                String target = stateSnapshot.getText();
                                if (!isValidName(target)) {
                                    return List.of(
                                            AnvilGUI.ResponseAction.replaceInputText("Try again!"),
                                            AnvilGUI.ResponseAction.run(() ->
                                                    PlayerUtils.playSound(clickedPlayer,
                                                            XSound.ENTITY_WANDERING_TRADER_NO,
                                                            XSound.ENTITY_VILLAGER_NO))
                                    );
                                }
                                return List.of(AnvilGUI.ResponseAction.close());
                            })
                            .onClose(stateSnapshot -> {
                                String target = stateSnapshot.getText();
                                if (!isValidName(target)) {
                                    clickedPlayer.sendMessage(Message.WHITELIST_NAME_NOT_VALID.toString());
                                    return;
                                }
                                if (gameManager.getRegistry().getWhitelistPlayers().contains(target)) {
                                    clickedPlayer.sendMessage(Message.WHITELIST_PLAYER_ALREADY_WHITELISTED.toString()
                                            .replace("<player>", target));
                                    return;
                                }
                                gameManager.getRegistry().getWhitelistPlayers().add(target);
                                PlayerUtils.playSound(clickedPlayer, XSound.ENTITY_EXPERIENCE_ORB_PICKUP);
                                clickedPlayer.sendMessage(Message.WHITELIST_ADD.toString()
                                        .replace("<player>", target));
                            })
                            .text("Enter Player Name")
                            .plugin(BukkitPlugin.INSTANCE)
                            .open(clickedPlayer);
                }
        );
    }

    private MenuItem buildWhitelistToggleButton() {
        return MenuItem.of(
                buildToggleItem(XMaterial.RAIL, "&6Whitelist", gameManager.getRegistry().isWhitelist()),
                (clickedPlayer, clickType) -> {
                    if (!clickedPlayer.hasPermission(Permission.HOST_PERMISSION)) return;
                    playClick(clickedPlayer);
                    gameManager.getRegistry().setWhitelist(clickedPlayer, !gameManager.getRegistry().isWhitelist());
                    open(clickedPlayer);
                }
        );
    }

    private MenuItem buildWhitelistedPlayerButton() {
        return MenuItem.of(
                ItemBuilder.of(Utilities.createSkull("http://textures.minecraft.net/texture/92607d664798962e10e8d7942b35f07119f8b4b846bcd53deeba1e532747b46"))
                        .name("&dWhitelisted Players")
                        .lore(
                                " ",
                                "&7Click to show all Whitelisted Players",
                                " "
                        ).build(),
                (clickedPlayer, clickType) -> {
                    if (!clickedPlayer.hasPermission(Permission.HOST_PERMISSION)) return;
                    playClick(clickedPlayer);
                    new WhitelistMenu().open(clickedPlayer);
                }
        );
    }

    private MenuItem buildPracticeButton() {
        return MenuItem.of(
                buildToggleItem(XMaterial.DIAMOND_SWORD, "&bPractice Arena", practiceManager.isOpened()),
                (clickedPlayer, clickType) -> {
                    if (!clickedPlayer.hasPermission(Permission.HOST_PERMISSION)) return;
                    playClick(clickedPlayer);
                    practiceManager.setOpened(!practiceManager.isOpened(), clickedPlayer);
                    open(clickedPlayer);
                }
        );
    }

    private MenuItem buildWorldEditorButton() {
        return MenuItem.of(
                ItemBuilder.of(XMaterial.GRASS_BLOCK)
                        .name("&aWorld Editor")
                        .lore(
                                " ",
                                "&7Click to open World Editor",
                                "&7menu.",
                                " "
                        )
                        .build(),
                (clickedPlayer, clickType) -> {
                    if (!clickedPlayer.hasPermission(Permission.HOST_PERMISSION)) return;
                    playClick(clickedPlayer);
                    new WorldEditorMenu().open(clickedPlayer);
                }
        );
    }

    private MenuItem buildSaveConfigsButton() {
        return MenuItem.of(
                ItemBuilder.of(XMaterial.ANVIL)
                        .name("&6Save Configurations")
                        .lore(
                                " ",
                                "&7Click to save or load",
                                "&7config and scenario.",
                                " "
                        )
                        .build(),
                (clickedPlayer, clickType) -> {
                    if (!clickedPlayer.hasPermission(Permission.HOST_PERMISSION)) return;
                    playClick(clickedPlayer);
                    new SaveConfigsMenu().open(clickedPlayer);
                }
        );
    }

    private MenuItem buildConfigButton() {
        return MenuItem.of(
                ItemBuilder.of(XMaterial.BOOK)
                        .name("&6Configuration")
                        .lore(
                                " ",
                                "&7Click to open uhc game",
                                "&7configuration menu.",
                                " "
                        )
                        .build(),
                (clickedPlayer, clickType) -> {
                    if (!clickedPlayer.hasPermission(Permission.HOST_PERMISSION)) return;
                    playClick(clickedPlayer);
                    new ConfigMenu(gameManager.getGame().getSetting()).open(clickedPlayer);
                }
        );
    }

    private MenuItem buildScenariosButton() {
        return MenuItem.of(
                ItemBuilder.of(XMaterial.PAPER)
                        .name("&2Scenarios")
                        .lore(
                                " ",
                                "&7Click to open toggleable",
                                "&7scenarios menu",
                                " "
                        )
                        .build(),
                (clickedPlayer, clickType) -> {
                    if (!clickedPlayer.hasPermission(Permission.HOST_PERMISSION)) return;
                    playClick(clickedPlayer);
                    new ScenariosAdminMenu().open(clickedPlayer);
                }
        );
    }

    @SuppressWarnings("deprecation")
    private ItemStack buildToggleItem(XMaterial material, String name, boolean enabled) {
        return ItemBuilder.of(material)
                .name(name)
                .lore(
                        " ",
                        "&7Click to toggle " + ChatColor.stripColor(name),
                        " ",
                        enabled ? "&e» &aEnabled" : "&a  Enabled",
                        enabled ? "&c  Disabled" : "&e» &cDisabled",
                        " "
                )
                .build();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isValidName(String name) {
        return name != null && name.matches("^\\w{3,16}$");
    }
}
