package me.lotiny.misty.bukkit.provider.menus.staff.configuration;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import io.fairyproject.bukkit.gui.Gui;
import io.fairyproject.bukkit.gui.pane.NormalPane;
import io.fairyproject.bukkit.gui.pane.PaginatedPane;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import io.fairyproject.util.CC;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameSetting;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.bukkit.Permission;
import me.lotiny.misty.bukkit.game.GameSettingImpl;
import me.lotiny.misty.bukkit.provider.menus.MenuItem;
import me.lotiny.misty.bukkit.provider.menus.MistyPaginatedMenu;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class SaveConfigsMenu extends MistyPaginatedMenu {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final int SLOT_CREATE = 22;
    @Autowired
    private static GameManager gameManager;
    @Autowired
    private static ScenarioManager scenarioManager;

    @Override
    public Component getTitle(Player player) {
        return Component.text("Saved Configurations");
    }

    @Override
    public int getRows(Player player) {
        return 3;
    }

    @Override
    public List<MenuItem> getButtons(Player player, PaginatedPane pane, Gui gui) {
        return gameManager.getGameSettingMap().values().stream()
                .map(this::buildConfigButton)
                .collect(Collectors.toList());
    }

    private MenuItem buildConfigButton(GameSetting setting) {
        return MenuItem.of(
                ItemBuilder.of(XMaterial.PAPER)
                        .name(setting.getConfigName())
                        .lore(getLore(setting))
                        .build(),
                (player, clickType) -> {
                    if (!player.hasPermission(Permission.HOST_PERMISSION)) return;
                    if (clickType.isLeftClick()) {
                        gameManager.loadGame(setting.getConfigId());
                        player.sendMessage(CC.translate("&eYou've loaded game &b" + setting.getConfigName() + "&e."));
                        player.closeInventory();
                    } else if (clickType.isRightClick()) {
                        new ConfigEditorMenu(setting).open(player);
                    }
                }
        );
    }

    @Override
    public Map<Integer, MenuItem> getBorderButtons(Player player, NormalPane topPane, NormalPane bottomPane, Gui gui) {
        return Map.of(
                SLOT_CREATE, MenuItem.of(
                        ItemBuilder.of(XMaterial.EMERALD_BLOCK)
                                .name("&aCreate New Config")
                                .lore("&7Click to save current config", "&7as new config.")
                                .build(),
                        (clickedPlayer, clickType) -> {
                            if (!player.hasPermission(Permission.HOST_PERMISSION)) return;
                            createNewConfig(clickedPlayer);
                        }
                )
        );
    }

    private void createNewConfig(Player player) {
        String formatDateTime = getCurrentFormattedDate();
        List<String> scenarios = scenarioManager.getEnabledScenarios(gameManager).stream()
                .map(Scenario::getName)
                .toList();

        GameSetting original = gameManager.getGame().getSetting();
        GameSetting newSetting = new GameSettingImpl(original);

        newSetting.setConfigId(UUID.randomUUID());
        newSetting.setDef(false);
        newSetting.setSavedBy(player.getName());
        newSetting.setSavedDate(formatDateTime);
        newSetting.setEnabledScenarios(scenarios);
        newSetting.setConfigName("Config-" + System.currentTimeMillis());

        gameManager.getGameSettingMap().put(newSetting.getConfigId(), newSetting);
        gameManager.saveGame(newSetting);

        PlayerUtils.playSound(player, XSound.ENTITY_EXPERIENCE_ORB_PICKUP);
        player.sendMessage(CC.GREEN + "Created new config!");
        player.closeInventory();
    }

    private String getCurrentFormattedDate() {
        LocalDateTime now = LocalDateTime.now(gameManager.getRegistry().getZoneId());
        return now.format(DATE_FORMATTER);
    }

    private List<String> getLore(GameSetting setting) {
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add("&fConfig Id&7: &b" + setting.getConfigId());
        lore.add("&fDefault&7: &b" + setting.isDef());
        lore.add(" ");
        lore.add("&fTeam Size&7: &b" + setting.getTeamSize());
        lore.add("&fScenarios&7:");

        if (setting.getEnabledScenarios().isEmpty()) {
            lore.add("  &7- &cNone");
        } else {
            setting.getEnabledScenarios().forEach(s -> lore.add("  &7- &b" + s));
        }

        lore.add(" ");
        lore.add("&fSaved By&7: &b" + setting.getSavedBy());
        lore.add("&fSaved Date&7: &b" + setting.getSavedDate());
        lore.add(" ");
        lore.add("&7Left-click to use this game config.");
        lore.add("&7Right-click to open editor menu.");
        lore.add(" ");
        return lore;
    }
}
