package me.lotiny.misty.api.game;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.BiConsumer;
import java.util.function.Function;

@RequiredArgsConstructor
public enum ConfigType {

    GAME_TYPE(XMaterial.NAME_TAG, Integer.class, (game, val) -> game.setMaxTeamSize((int) val), GameSetting::getTeamSize),
    FINAL_HEAL(XMaterial.GLISTERING_MELON_SLICE, Integer.class, (game, val) -> game.setFinalHeal((int) val), GameSetting::getFinalHeal),
    GRACE_PERIOD(XMaterial.DIAMOND_SWORD, Integer.class, (game, val) -> game.setGracePeriod((int) val), GameSetting::getGracePeriod),
    BORDER_SIZE(XMaterial.BEDROCK, Integer.class, (game, val) -> game.setBorderSize((int) val), GameSetting::getBorderSize),
    FIRST_SHRINK(XMaterial.COBBLESTONE_WALL, Integer.class, (game, val) -> game.setFirstShrink((int) val), GameSetting::getFirstShrink),
    APPLE_RATE(XMaterial.APPLE, Integer.class, (game, val) -> game.setAppleRate((int) val), GameSetting::getAppleRate),

    SHEARS(XMaterial.SHEARS, Boolean.class, (game, val) -> game.setShears((boolean) val), GameSetting::isShears),
    NETHER(XMaterial.NETHERRACK, Boolean.class, (game, val) -> game.setNether((boolean) val), GameSetting::isNether),
    NETHER_TIME(XMaterial.NETHER_BRICK_FENCE, Integer.class, (game, val) -> game.setNetherTime((int) val), GameSetting::getNetherTime),
    BED_BOMB(XMaterial.RED_BED, Boolean.class, (game, val) -> game.setBedBomb((boolean) val), GameSetting::isBedBomb),
    GOD_APPLE(XMaterial.ENCHANTED_GOLDEN_APPLE, Boolean.class, (game, val) -> game.setGodApple((boolean) val), GameSetting::isGodApple),
    LAST_BORDER_FLAT(XMaterial.GRASS_BLOCK, Boolean.class, (game, val) -> game.setLastBorderFlat((boolean) val), GameSetting::isLastBorderFlat),
    LATE_SCATTER(XMaterial.CLOCK, Boolean.class, (game, val) -> game.setLateScatter((boolean) val), GameSetting::isLateScatter),
    PEARL_DAMAGE(XMaterial.ENDER_PEARL, Boolean.class, (game, val) -> game.setPearlDamage((boolean) val), GameSetting::isPearlDamage),
    CHAT_BEFORE_PVP(XMaterial.OAK_SIGN, Boolean.class, (game, val) -> game.setChatBeforePvp((boolean) val), GameSetting::isChatBeforePvp),

    SPEED_1(XMaterial.POTION, Boolean.class, (game, val) -> game.setSpeed1((boolean) val), GameSetting::isSpeed1),
    SPEED_2(XMaterial.POTION, Boolean.class, (game, val) -> game.setSpeed2((boolean) val), GameSetting::isSpeed2),
    STRENGTH_1(XMaterial.POTION, Boolean.class, (game, val) -> game.setStrength1((boolean) val), GameSetting::isStrength1),
    STRENGTH_2(XMaterial.POTION, Boolean.class, (game, val) -> game.setStrength2((boolean) val), GameSetting::isStrength2),
    POISON(XMaterial.POTION, Boolean.class, (game, val) -> game.setPoison((boolean) val), GameSetting::isPoison),
    INVISIBLE(XMaterial.POTION, Boolean.class, (game, val) -> game.setInvisible((boolean) val), GameSetting::isInvisible);

    @Getter
    private final XMaterial material;
    @Getter
    private final Class<?> expectedType;
    private final BiConsumer<GameSetting, Object> applier;
    private final Function<GameSetting, Object> extractor;

    public void apply(GameSetting setting, Object value) {
        if (!expectedType.isInstance(value)) {
            throw new IllegalArgumentException(
                    "Expected " + expectedType.getSimpleName() +
                            " but got " + value.getClass().getSimpleName()
            );
        }
        applier.accept(setting, value);
    }

    public Object get(GameSetting setting) {
        return extractor.apply(setting);
    }
}
