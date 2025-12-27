package me.lotiny.misty.bukkit.config.impl;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.lotiny.misty.bukkit.config.BaseConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Configuration
public final class ScenarioConfig extends BaseConfig {

    private ForbiddenAlchemy forbiddenAlchemy = new ForbiddenAlchemy();

    private FlowerPower flowerPower = new FlowerPower();

    private List<Homework> homework = List.of(
            new Homework(XMaterial.DIRT, 32, 256),
            new Homework(XMaterial.GLASS, 1, 64),
            new Homework(XMaterial.TROPICAL_FISH, 1, 4),
            new Homework(XMaterial.ROTTEN_FLESH, 1, 8),
            new Homework(XMaterial.SPIDER_EYE, 1, 2),
            new Homework(XMaterial.BONE, 1, 8),
            new Homework(XMaterial.GUNPOWDER, 1, 3),
            new Homework(XMaterial.PAPER, 8, 32),
            new Homework(XMaterial.IRON_INGOT, 4, 32),
            new Homework(XMaterial.WHITE_WOOL, 1, 8),
            new Homework(XMaterial.COOKED_CHICKEN, 1, 16),
            new Homework(XMaterial.COAL, 1, 64),
            new Homework(XMaterial.COBBLESTONE, 64, 256),
            new Homework(XMaterial.COBBLESTONE_STAIRS, 1, 30),
            new Homework(XMaterial.APPLE, 4, 32),
            new Homework(XMaterial.CHEST, 8, 64),
            new Homework(XMaterial.RAIL, 1, 64),
            new Homework(XMaterial.REDSTONE_BLOCK, 2, 6),
            new Homework(XMaterial.LEVER, 24, 64),
            new Homework(XMaterial.LADDER, 12, 64),
            new Homework(XMaterial.BOOK, 1, 6),
            new Homework(XMaterial.LEATHER, 1, 20),
            new Homework(XMaterial.SADDLE, 1, 1),
            new Homework(XMaterial.ITEM_FRAME, 1, 10),
            new Homework(XMaterial.COMPASS, 1, 3),
            new Homework(XMaterial.SAND, 64, 256),
            new Homework(XMaterial.STONE, 8, 64),
            new Homework(XMaterial.SUGAR, 1, 32),
            new Homework(XMaterial.CLOCK, 1, 3),
            new Homework(XMaterial.DIAMOND, 1, 4)
    );

    @Getter
    @Configuration
    public static class ForbiddenAlchemy {

        @Comment("The overall percentage chance (0–100) that mining Redstone Ore will drop a brewing ingredient.")
        private int chance = 10;

        @Comment({
                "A weighted table of brewing ingredients and their relative drop chances.",
                "The values represent weights, not exact percentages — higher numbers = more likely.",
                "For example, with the defaults:",
                "- NETHER_WART has weight 20 → highest chance to drop.",
                "- SUGAR_CANE has weight 5 → lower chance to drop.",
                "All weights are summed, and the probability is calculated proportionally."
        })
        private Map<XMaterial, Integer> ingredients = Map.of(
                XMaterial.BLAZE_ROD, 15,
                XMaterial.BLAZE_POWDER, 17,
                XMaterial.SUGAR, 13,
                XMaterial.SUGAR_CANE, 5,
                XMaterial.GLASS_BOTTLE, 10,
                XMaterial.BREWING_STAND, 10,
                XMaterial.GUNPOWDER, 10,
                XMaterial.NETHER_WART, 20
        );
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Configuration
    public static class Homework {

        private XMaterial material;
        private int minAmount;
        private int maxAmount;
    }

    @Getter
    @Configuration
    public static class FlowerPower {

        @Comment({
                "The amount of experience points given when Flower Power drops.",
                "0 = no experience, 1 = 1 XP, 2 = 2 XP, etc."
        })
        private int expDrop = 1;

        @Comment("The maximum number of items that can drop at once.")
        private int maxDrop = 64;

        @Comment({
                "Items that are not allowed to be dropped by Flower Power.",
                "You can also list individual items using their Material name."
        })
        private List<XMaterial> banned = createDefaultBannedList();

        private List<XMaterial> createDefaultBannedList() {
            List<XMaterial> mats = new ArrayList<>();

            mats.add(XMaterial.BEDROCK);
            mats.add(XMaterial.COMMAND_BLOCK);
            mats.add(XMaterial.COMMAND_BLOCK_MINECART);
            mats.add(XMaterial.CHAIN_COMMAND_BLOCK);
            mats.add(XMaterial.REPEATING_COMMAND_BLOCK);
            mats.add(XMaterial.END_PORTAL_FRAME);
            mats.add(XMaterial.BARRIER);
            mats.add(XMaterial.PLAYER_HEAD);
            mats.addAll(XTag.FLOWERS.getValues());
            mats.addAll(XTag.BANNERS.getValues());
            mats.addAll(XTag.SPAWN_EGGS.getValues());
            mats.addAll(XTag.GLASS.getValues());
            mats.addAll(XTag.BEDS.getValues());
            mats.addAll(XTag.WOOL.getValues());
            mats.addAll(XTag.CARPETS.getValues());

            return mats;
        }
    }
}
