package me.lotiny.misty.nms.v1_21_11;

import me.lotiny.misty.nms.v1_21_4.v1_21_4;
import org.bukkit.GameRule;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;

public class v1_21_11 extends v1_21_4 {

    @Override
    public void setGameRule(World world, String rule, Object value) {
        GameRule<?> gameRule = Registry.GAME_RULE.get(NamespacedKey.minecraft(rule));
        me.lotiny.misty.shared.utils.GameRule.setGameRule(gameRule, world, rule, value);
    }
}
