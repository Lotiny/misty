package me.lotiny.misty.bukkit.utils.scenario;

import io.fairyproject.Fairy;
import io.fairyproject.container.Autowired;
import io.fairyproject.container.DependsOn;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.ConfigManager;
import me.lotiny.misty.bukkit.config.impl.ScenarioConfig;
import me.lotiny.misty.bukkit.utils.RandomSelector;

import java.util.HashSet;
import java.util.Set;

@DependsOn(ConfigManager.class)
@InjectableComponent
@RequiredArgsConstructor
public class HomeworkUtils {

    @Autowired
    private static HomeworkUtils instance;

    private Set<ScenarioConfig.Homework> homeworks;

    public static HomeworkUtils get() {
        return instance;
    }

    @PostInitialize
    public void onPostInit() {
        homeworks = new HashSet<>(Config.getScenarioConfig().getHomework());
    }

    public ScenarioConfig.Homework getRandomHomework() {
        RandomSelector<ScenarioConfig.Homework> selector = new RandomSelector<>(homeworks, Fairy.random());
        return selector.getRandomElement();
    }
}
