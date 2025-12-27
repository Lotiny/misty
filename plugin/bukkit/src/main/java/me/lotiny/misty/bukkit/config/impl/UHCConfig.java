package me.lotiny.misty.bukkit.config.impl;

import de.exlll.configlib.Configuration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.lotiny.misty.bukkit.config.BaseConfig;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Configuration
public final class UHCConfig extends BaseConfig {

    private Map<UUID, GameConfig> gameConfig = Map.of(
            UUID.randomUUID(),
            new GameConfig()
    );

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Configuration
    public static class GameConfig {

        private String name = "&bMisty &e(Default Config)";

        private boolean isDefault = true;

        private List<String> scenarios = List.of("Cut Clean", "No Clean");

        private Potion potion = new Potion();

        private Setting setting = new Setting();

        private String savedBy = "Lotiny";

        private LocalDate savedDate = LocalDate.now();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Configuration
    public static class Potion {

        private boolean speed1 = true;

        private boolean speed2 = true;

        private boolean strength1 = false;

        private boolean strength2 = false;

        private boolean poison = true;

        private boolean invisible = false;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Configuration
    public static class Setting {

        private int teamSize = 1;

        private int finalHeal = 10;

        private int gracePeriod = 20;

        private int borderSize = 1000;

        private int firstShrink = 35;

        private boolean lastBorderFlat = true;

        private int appleRate = 2;

        private boolean shears = true;

        private boolean lateScatter = true;

        private boolean godApple = false;

        private boolean enderPearlDamage = true;

        private boolean chatBeforePvp = false;

        private boolean nether = false;

        private int netherTime = 20;

        private boolean bedBomb = false;
    }
}
