package me.lotiny.misty.bukkit.provider.menus.staff;

import io.fairyproject.bukkit.gui.Gui;
import io.fairyproject.bukkit.gui.pane.NormalPane;
import io.fairyproject.bukkit.gui.pane.PaginatedPane;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.bukkit.Permission;
import me.lotiny.misty.bukkit.provider.menus.MenuItem;
import me.lotiny.misty.bukkit.provider.menus.MistyPaginatedMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScenariosAdminMenu extends MistyPaginatedMenu {

    @Autowired
    private static GameManager gameManager;
    @Autowired
    private static ScenarioManager scenarioManager;

    @Override
    public Component getTitle(Player player) {
        return Component.text("Scenarios");
    }

    @Override
    public int getRows(Player player) {
        return 6;
    }

    @Override
    public List<MenuItem> getButtons(Player player, PaginatedPane pane, Gui gui) {
        List<MenuItem> buttons = new ArrayList<>();

        for (Scenario scenario : scenarioManager.getScenarios()) {
            buttons.add(MenuItem.of(
                    buildScenarioItem(scenario),
                    (clickedPlayer, clickType) -> {
                        if (!clickedPlayer.hasPermission(Permission.HOST_PERMISSION)) return;

                        toggleScenario(scenario, clickedPlayer);
                        playClick(clickedPlayer);
                        open(clickedPlayer, pane.getPage());
                    }
            ));
        }

        return buttons;
    }

    @Override
    public Map<Integer, MenuItem> getBorderButtons(Player player, NormalPane topPane, NormalPane bottomPane, Gui gui) {
        return Map.of();
    }

    private void toggleScenario(Scenario scenario, Player player) {
        if (scenario.isEnabled()) {
            scenarioManager.disable(scenario, gameManager, player, true);
        } else {
            scenarioManager.enable(scenario, gameManager, player, true);
        }
    }

    private ItemStack buildScenarioItem(Scenario scenario) {
        ItemBuilder item = ItemBuilder.of(scenario.getIcon());

        item.name("&b" + scenario.getName());
        item.lore(getToggleLore(scenario.isEnabled()));

        return item.build();
    }

    private List<String> getToggleLore(boolean enabled) {
        return enabled
                ? List.of(" ", "&e» &aEnabled", "&c  Disabled", " ")
                : List.of(" ", "&a  Enabled", "&e» &cDisabled", " ");
    }
}
