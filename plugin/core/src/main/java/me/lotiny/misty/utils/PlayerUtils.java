package me.lotiny.misty.utils;

import com.cryptomorin.xseries.XSound;
import lombok.experimental.UtilityClass;
import me.lotiny.misty.nms.NMS;
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
        return NMS.getInstance().getMaxHealth(player);
    }

    public void setMaxHealth(Player player, double health) {
        NMS.getInstance().setMaxHealth(player, health);
    }

    public ItemStack getItemInHand(Player player) {
        return NMS.getInstance().getItemInHand(player);
    }

    public void setItemInHand(Player player, ItemStack item) {
        NMS.getInstance().setItemInHand(player, item);
    }

    public ItemStack getItemInOffHand(Player player) {
        return NMS.getInstance().getItemInOffHand(player);
    }

    public void setItemInOffHand(Player player, ItemStack item) {
        NMS.getInstance().setItemInOffHand(player, item);
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
