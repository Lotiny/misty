package me.lotiny.misty.api.scenario;

import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.task.AbstractScheduleTask;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ScenarioManager {

    void registerScenarios();

    void dropScenarioItems(Location location);

    List<ItemStack> getDroppedItems();

    List<Scenario> getEnabledScenarios(GameManager gameManager);

    List<Scenario> getEnabledScenarios(AbstractScheduleTask startTask);

    List<Scenario> getScenarios();

    List<String> getScenariosToEnable();

    Scenario getScenario(String scenario);

    boolean isEnabled(String scenario);

    void enable(Scenario scenario, GameManager gameManager, CommandSender sender, boolean messageLog);

    void disable(Scenario scenario, GameManager gameManager, CommandSender sender, boolean messageLog);
}
