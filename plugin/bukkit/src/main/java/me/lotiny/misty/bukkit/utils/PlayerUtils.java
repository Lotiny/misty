package me.lotiny.misty.bukkit.utils;

import com.cryptomorin.xseries.XSound;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class PlayerUtils {

    public void healPlayer(Player player) {
        player.setHealth(getMaxHealth(player));
    }

    public double getMaxHealth(Player player) {
        return ReflectionUtils.get().getMaxHealth(player);
    }

    public void setMaxHealth(Player player, double health) {
        ReflectionUtils.get().setMaxHealth(player, health);
    }

    public ItemStack getItemInHand(Player player) {
        return ReflectionUtils.get().getItemInHand(player);
    }

    public void setItemInHand(Player player, ItemStack item) {
        ReflectionUtils.get().setItemInHand(player, item);
    }

    public ItemStack getItemInOffHand(Player player) {
        return ReflectionUtils.get().getItemInOffHand(player);
    }

    public void setItemInOffHand(Player player, ItemStack item) {
        ReflectionUtils.get().setItemInOffHand(player, item);
    }

    public void playSound(XSound sound, XSound fallback) {
        Bukkit.getOnlinePlayers().forEach(player -> playSound(player, sound, fallback));
    }

    public void playSound(XSound sound) {
        Bukkit.getOnlinePlayers().forEach(player -> playSound(player, sound));
    }

    public void playSound(Player player, XSound sound, XSound fallback) {
        playSound(player, sound.isSupported() ? sound : fallback);
    }

    public void playSound(Player player, XSound sound) {
        sound.play(player);
    }

    public void playSound(Location location, XSound sound, XSound fallback) {
        playSound(location, sound.isSupported() ? sound : fallback);
    }

    public void playSound(Location location, XSound sound) {
        sound.play(location);
    }
}
