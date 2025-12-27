package me.lotiny.misty.bukkit.utils.cooldown;

import io.fairyproject.bootstrap.bukkit.BukkitPlugin;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class PlayerCooldown extends io.fairyproject.bukkit.timer.PlayerCooldown {

    private final DecimalFormat secondFormat;

    public PlayerCooldown(long defaultCooldown) {
        super(defaultCooldown, BukkitPlugin.INSTANCE);
        this.secondFormat = new DecimalFormat("#0.0");
    }

    @Override
    public long getCooldown(Player player) {
        final Long value = this.getCache().get(player);
        return value != null ? Math.abs(System.currentTimeMillis() - value) : -1;
    }

    public String getCooldownMillis(Player player) {
        return this.formatSeconds(this.getCooldown(player));
    }

    private String formatSeconds(long time) {
        return secondFormat.format((float) time / 1000.0F);
    }
}
