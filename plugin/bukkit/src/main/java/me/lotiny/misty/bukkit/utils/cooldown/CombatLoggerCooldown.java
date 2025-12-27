package me.lotiny.misty.bukkit.utils.cooldown;

import io.fairyproject.util.Cooldown;
import io.fairyproject.util.FormatUtil;
import me.lotiny.misty.api.game.registry.CombatLogger;

import java.util.function.Consumer;

public class CombatLoggerCooldown extends Cooldown<CombatLogger> {

    public CombatLoggerCooldown(long defaultCooldown, Consumer<CombatLogger> removalListener) {
        super(defaultCooldown, removalListener);
    }

    @Override
    public long getCooldown(CombatLogger combatLogger) {
        final Long value = this.getCache().get(combatLogger);
        return value != null ? Math.abs(System.currentTimeMillis() - value) : -1;
    }

    public String getCooldownTimer(CombatLogger combatLogger) {
        return FormatUtil.formatMillis(this.getCooldown(combatLogger));
    }
}
