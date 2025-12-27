package me.lotiny.misty.bukkit.utils.scenario;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.bukkit.utils.VersionUtils;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum OreType {

    COAL_ORE(XMaterial.COAL_ORE, XMaterial.COAL, XMaterial.COAL, XMaterial.COAL, 1, 1),
    DEEPSLATE_COAL_ORE(XMaterial.DEEPSLATE_COAL_ORE, XMaterial.COAL, XMaterial.COAL, XMaterial.COAL, 1, 1),

    IRON_ORE(XMaterial.IRON_ORE, XMaterial.IRON_ORE, XMaterial.RAW_IRON, XMaterial.IRON_INGOT, 1, 1),
    DEEPSLATE_IRON_ORE(XMaterial.DEEPSLATE_IRON_ORE, XMaterial.DEEPSLATE_IRON_ORE, XMaterial.RAW_IRON, XMaterial.IRON_INGOT, 1, 1),
    RAW_IRON_BLOCK(XMaterial.RAW_IRON_BLOCK, XMaterial.RAW_IRON_BLOCK, XMaterial.RAW_IRON_BLOCK, XMaterial.IRON_BLOCK, 1, 9),

    GOLD_ORE(XMaterial.GOLD_ORE, XMaterial.GOLD_ORE, XMaterial.RAW_GOLD, XMaterial.GOLD_INGOT, 1, 3),
    DEEPSLATE_GOLD_ORE(XMaterial.DEEPSLATE_GOLD_ORE, XMaterial.DEEPSLATE_GOLD_ORE, XMaterial.RAW_GOLD, XMaterial.GOLD_INGOT, 1, 3),
    RAW_GOLD_BLOCK(XMaterial.RAW_GOLD_BLOCK, XMaterial.RAW_GOLD_BLOCK, XMaterial.RAW_GOLD_BLOCK, XMaterial.GOLD_BLOCK, 1, 27),
    NETHER_GOLD_ORE(XMaterial.NETHER_GOLD_ORE, XMaterial.GOLD_NUGGET, XMaterial.GOLD_NUGGET, XMaterial.GOLD_INGOT, 3, 1),

    COPPER_ORE(XMaterial.COPPER_ORE, XMaterial.COPPER_ORE, XMaterial.RAW_COPPER, XMaterial.COPPER_INGOT, 2, 1),
    DEEPSLATE_COPPER_ORE(XMaterial.DEEPSLATE_COPPER_ORE, XMaterial.DEEPSLATE_COPPER_ORE, XMaterial.RAW_COPPER, XMaterial.COPPER_INGOT, 2, 1),
    RAW_COPPER_BLOCK(XMaterial.RAW_COPPER_BLOCK, XMaterial.RAW_COPPER_BLOCK, XMaterial.RAW_COPPER_BLOCK, XMaterial.COPPER_BLOCK, 1, 9),

    REDSTONE_ORE(XMaterial.REDSTONE_ORE, XMaterial.REDSTONE, XMaterial.REDSTONE, XMaterial.REDSTONE, 4, 2),
    DEEPSLATE_REDSTONE_ORE(XMaterial.DEEPSLATE_REDSTONE_ORE, XMaterial.REDSTONE, XMaterial.REDSTONE, XMaterial.REDSTONE, 4, 2),

    LAPIS_ORE(XMaterial.LAPIS_ORE, XMaterial.LAPIS_LAZULI, XMaterial.LAPIS_LAZULI, XMaterial.LAPIS_LAZULI, 5, 2),
    DEEPSLATE_LAPIS_ORE(XMaterial.DEEPSLATE_LAPIS_ORE, XMaterial.LAPIS_LAZULI, XMaterial.LAPIS_LAZULI, XMaterial.LAPIS_LAZULI, 5, 2),

    QUARTZ_ORE(XMaterial.NETHER_QUARTZ_ORE, XMaterial.QUARTZ, XMaterial.QUARTZ, XMaterial.QUARTZ, 1, 2),

    DIAMOND_ORE(XMaterial.DIAMOND_ORE, XMaterial.DIAMOND, XMaterial.DIAMOND, XMaterial.DIAMOND, 1, 4),
    DEEPSLATE_DIAMOND_ORE(XMaterial.DEEPSLATE_DIAMOND_ORE, XMaterial.DIAMOND, XMaterial.DIAMOND, XMaterial.DIAMOND, 1, 4),

    EMERALD_ORE(XMaterial.EMERALD_ORE, XMaterial.EMERALD, XMaterial.EMERALD, XMaterial.EMERALD, 1, 4),
    DEEPSLATE_EMERALD_ORE(XMaterial.DEEPSLATE_EMERALD_ORE, XMaterial.EMERALD, XMaterial.EMERALD, XMaterial.EMERALD, 1, 4);

    private final XMaterial input;
    private final XMaterial outputLegacy;
    private final XMaterial outputModern;
    private final XMaterial smelted;
    private final int baseAmount;
    private final int exp;

    public static Optional<OreType> get(XMaterial material) {
        return Arrays.stream(OreType.values())
                .filter(oreType -> oreType.getInput() == material)
                .findFirst();
    }

    public XMaterial getOutput() {
        if (VersionUtils.is(8, 8)) {
            return this.outputLegacy;
        } else {
            return this.outputModern;
        }
    }
}
