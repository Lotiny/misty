package me.lotiny.misty.shared.utils;

import io.fairyproject.util.ConditionUtils;
import lombok.experimental.UtilityClass;
import org.bukkit.World;

@UtilityClass
public class GameRule {

    @SuppressWarnings("unchecked")
    public void setGameRule(org.bukkit.GameRule<?> gameRule, World world, String rule, Object value) {
        ConditionUtils.notNull(gameRule, "Game rule '" + rule + "' not found");

        try {
            if (value instanceof Boolean && gameRule.getType() == Boolean.class) {
                world.setGameRule((org.bukkit.GameRule<Boolean>) gameRule, (Boolean) value);
            } else if (value instanceof Integer && gameRule.getType() == Integer.class) {
                world.setGameRule((org.bukkit.GameRule<Integer>) gameRule, (Integer) value);
            } else {
                throw new IllegalArgumentException("Invalid value type for game rule '" + rule + "'");
            }
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Type mismatch for game rule '" + rule + "'");
        }
    }
}
