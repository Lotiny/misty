package me.lotiny.misty.bukkit.config;

import io.fairyproject.bootstrap.bukkit.BukkitPlugin;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.container.PreDestroy;
import io.fairyproject.log.Log;
import lombok.Getter;
import me.lotiny.misty.bukkit.config.impl.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@InjectableComponent
public class ConfigManager {

    private final Map<Class<? extends BaseConfig>, BaseConfig> configs = new HashMap<>();

    @PostInitialize
    public void onPostInit() {
        register(new HotBarConfig(), "hotbar.yml");
        register(new MainConfig(), "config.yml");
        register(new MessageConfig(), "message.yml");
        register(new PracticeConfig(), "practice.yml");
        register(new ScenarioConfig(), "scenario.yml");
        register(new ScoreboardConfig(), "scoreboard.yml");
        register(new StorageConfig(), "storage.yml");
        register(new UHCConfig(), "uhc-setting.yml");
        loadAll();

        Config.init();
    }

    @PreDestroy
    public void onPreDestroy() {
        saveAll();
    }

    public <T extends BaseConfig> void register(T config, String fileName) {
        config.setup(BukkitPlugin.INSTANCE, fileName);
        configs.put(config.getClass(), config);
    }

    public void loadAll() {
        configs.replaceAll((clazz, config) -> config.load());
        Log.info("Loaded " + configs.size() + " configurations.");
    }

    public void saveAll() {
        configs.values().forEach(BaseConfig::save);
    }

    public <T extends BaseConfig> T get(Class<T> type) {
        BaseConfig config = configs.get(type);
        if (config == null) {
            throw new IllegalArgumentException("Config not found: " + type.getName());
        }
        return type.cast(config);
    }
}
