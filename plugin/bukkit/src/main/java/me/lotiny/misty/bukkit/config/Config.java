package me.lotiny.misty.bukkit.config;

import io.fairyproject.container.Autowired;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import me.lotiny.misty.bukkit.config.impl.*;

@UtilityClass
public class Config {

    @Getter
    private HotBarConfig hotBarConfig;
    @Getter
    private MainConfig mainConfig;
    @Getter
    private MessageConfig messageConfig;
    @Getter
    private PracticeConfig practiceConfig;
    @Getter
    private ScenarioConfig scenarioConfig;
    @Getter
    private ScoreboardConfig scoreboardConfig;
    @Getter
    private StorageConfig storageConfig;
    @Getter
    private UHCConfig uhcConfig;

    @Autowired
    private ConfigManager configManager;

    public void init() {
        hotBarConfig = configManager.get(HotBarConfig.class);
        mainConfig = configManager.get(MainConfig.class);
        messageConfig = configManager.get(MessageConfig.class);
        practiceConfig = configManager.get(PracticeConfig.class);
        scenarioConfig = configManager.get(ScenarioConfig.class);
        scoreboardConfig = configManager.get(ScoreboardConfig.class);
        storageConfig = configManager.get(StorageConfig.class);
        uhcConfig = configManager.get(UHCConfig.class);
    }
}
