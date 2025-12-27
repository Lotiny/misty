package me.lotiny.misty.bukkit.utils.cooldown;

import io.fairyproject.util.Cooldown;
import lombok.Getter;
import me.lotiny.misty.api.team.Team;

import java.text.DecimalFormat;

@Getter
public class CombatCooldown extends Cooldown<Team> {

    private final DecimalFormat secondFormat;
    private final Team team;

    public CombatCooldown(long defaultCooldown, Team team) {
        super(defaultCooldown);
        this.secondFormat = new DecimalFormat("#0.0");
        this.team = team;
    }

    @Override
    public long getCooldown(Team team) {
        final Long value = this.getCache().get(team);
        return value != null ? Math.abs(System.currentTimeMillis() - value) : -1;
    }

    public String getCooldownMillis(Team team) {
        return this.formatSeconds(this.getCooldown(team));
    }

    private String formatSeconds(long time) {
        return secondFormat.format((float) time / 1000.0F);
    }
}
