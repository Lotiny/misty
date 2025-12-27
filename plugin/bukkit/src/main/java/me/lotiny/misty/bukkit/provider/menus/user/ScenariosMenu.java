package me.lotiny.misty.bukkit.provider.menus.user;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.gui.Gui;
import io.fairyproject.bukkit.gui.pane.NormalPane;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.bukkit.provider.menus.MenuItem;
import me.lotiny.misty.bukkit.provider.menus.MistyMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.*;

public class ScenariosMenu extends MistyMenu {

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
        List<Scenario> scenarios = new ArrayList<>(scenarioManager.getEnabledScenarios(gameManager));
        return Math.max((int) Math.ceil((double) scenarios.size() / 9), 1);
    }

    @Override
    public boolean isFilled(Player player) {
        return false;
    }

    @Override
    public Map<Integer, MenuItem> getButtons(Player player, NormalPane pane, Gui gui) {
        List<Scenario> scenarios = scenarioManager.getEnabledScenarios(gameManager);

        if (scenarios.isEmpty()) {
            return Collections.singletonMap(0,
                    MenuItem.of(
                            ItemBuilder.of(XMaterial.REDSTONE_BLOCK)
                                    .name("&cNone")
                                    .build()
                    ));
        }

        Map<Integer, MenuItem> buttons = HashMap.newHashMap(scenarios.size());

        for (int i = 0; i < scenarios.size(); i++) {
            buttons.put(i, MenuItem.of(scenarios.get(i).getIcon()));
        }

        return buttons;
    }
}
