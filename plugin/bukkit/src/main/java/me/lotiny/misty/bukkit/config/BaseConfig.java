package me.lotiny.misty.bukkit.config;

import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import lombok.Setter;
import me.lotiny.misty.bukkit.config.serializer.KitSerializer;
import me.lotiny.misty.bukkit.config.serializer.LocationSerializer;
import me.lotiny.misty.bukkit.config.serializer.PotionEffectSerializer;
import me.lotiny.misty.bukkit.kit.Kit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.nio.file.Path;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class BaseConfig {

    @Setter
    private transient Path configFile;
    @Setter
    private transient YamlConfigurationProperties properties;

    protected BaseConfig() {

    }

    public void setup(Plugin plugin, String fileName) {
        this.configFile = new File(plugin.getDataFolder(), fileName).toPath();
        this.properties = YamlConfigurationProperties.newBuilder()
                .setNameFormatter(NameFormatters.LOWER_UNDERSCORE)
                .addSerializer(Kit.class, new KitSerializer())
                .addSerializer(Location.class, new LocationSerializer())
                .addSerializer(PotionEffect.class, new PotionEffectSerializer())
                .build();
    }

    public void save() {
        if (configFile == null) {
            throw new IllegalStateException("Config not setup! Call setup() first.");
        }

        YamlConfigurations.save(configFile, (Class) getClass(), this, properties);
    }

    public <T extends BaseConfig> T load() {
        if (configFile == null) {
            throw new IllegalStateException("Config not setup! Call setup() first.");
        }

        T loadedConfig = (T) YamlConfigurations.update(configFile, (Class) getClass(), properties);

        loadedConfig.setConfigFile(this.configFile);
        loadedConfig.setProperties(this.properties);

        return loadedConfig;
    }
}
