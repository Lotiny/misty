package me.lotiny.misty.bukkit.command.parameters;

import io.fairyproject.bukkit.command.parameters.BukkitArgTransformer;
import io.fairyproject.container.InjectableComponent;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.bukkit.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@InjectableComponent
@RequiredArgsConstructor
public class ScenarioArgTransformer extends BukkitArgTransformer<Scenario> {

    private final ScenarioManager scenarioManager;

    @Override
    public Class[] type() {
        return new Class[]{Scenario.class};
    }

    @Override
    public Scenario transform(CommandSender sender, String source) {
        Scenario scenario = scenarioManager.getScenario(source);
        if (scenario == null) {
            return this.fail("No scenario with the name " + source + " found.");
        }

        return scenario;
    }

    @Override
    public List<String> tabComplete(Player player, String source) {
        return scenarioManager.getScenarios()
                .stream()
                .map(scenario -> StringUtils.rb(scenario.getName()))
                .toList();
    }
}
