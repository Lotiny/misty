package me.lotiny.misty.bukkit.kit;

import io.fairyproject.Fairy;
import io.fairyproject.container.DependsOn;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.container.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.ConfigManager;
import me.lotiny.misty.bukkit.config.impl.MainConfig;
import me.lotiny.misty.bukkit.utils.RandomSelector;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@DependsOn(ConfigManager.class)
@InjectableComponent
public class KitManager {

    @Getter
    private final Map<Integer, Kit> kits = new ConcurrentHashMap<>();

    @Setter
    private int defaultKit;
    @Getter
    private boolean random;

    private RandomSelector<Integer> randomSelector;

    @PostInitialize
    public void onPostInit() {
        MainConfig config = Config.getMainConfig();
        kits.putAll(config.getKit().getKits());
        defaultKit = config.getKit().getDefaultKit();
        random = config.getKit().isRandomKit();
        randomSelector = new RandomSelector<>(kits.keySet(), Fairy.random());
    }

    @PreDestroy
    public void onPreDestroy() {
        MainConfig config = Config.getMainConfig();
        config.getKit().setKits(kits);
        config.getKit().setDefaultKit(defaultKit);
        config.save();
    }

    public @Nullable Kit getKit(int id) {
        return kits.get(id);
    }

    public Kit getDefaultKit() {
        return this.getKit(defaultKit);
    }

    public int findAvailableId(Map<Integer, Kit> kitMap) {
        int id = 0;

        while (kitMap.containsKey(id)) {
            id++;
        }

        return id;
    }

    public Kit getRandomKit() {
        return kits.get(randomSelector.getRandomElement());
    }
}
