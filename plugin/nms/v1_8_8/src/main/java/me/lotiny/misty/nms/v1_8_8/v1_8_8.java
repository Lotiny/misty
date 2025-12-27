package me.lotiny.misty.nms.v1_8_8;

import com.cryptomorin.xseries.XPotion;
import io.fairyproject.log.Log;
import io.fairyproject.util.ConditionUtils;
import me.lotiny.misty.shared.LegacyReflectionAdapter;
import net.minecraft.server.v1_8_R3.Item;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

import java.lang.reflect.Method;

public final class v1_8_8 extends LegacyReflectionAdapter {

    @SuppressWarnings("deprecation")
    @Override
    public boolean isItem(Material material) {
        return material != null && Item.getById(material.getId()) != null;
    }

    @Override
    public XPotion getPotionEffect(ItemStack item) {
        if (item.getType() != Material.POTION) {
            return null;
        }

        Potion potion = Potion.fromItemStack(item);
        return XPotion.of(potion.getType());
    }

    @Override
    public int getPotionEffectLevel(ItemStack item) {
        return Potion.fromItemStack(item).getLevel();
    }

    @Override
    public ItemStack getItemInHand(Player player) {
        return player.getItemInHand();
    }

    @Override
    public void setItemInHand(Player player, ItemStack item) {
        player.setItemInHand(item);
    }

    @Override
    public ItemStack getItemInOffHand(Player player) {
        return null;
    }

    @Override
    public void setItemInOffHand(Player player, ItemStack item) {

    }

    @Override
    public double getMaxHealth(Player player) {
        return player.getMaxHealth();
    }

    @Override
    public void setMaxHealth(Player player, double health) {
        player.setMaxHealth(health);
    }

    @Override
    public void handleNetherPortal(PlayerPortalEvent event, World gameWorld, World netherWorld, int scale) {
        if (event.getTo() != null) return;

        Location location = event.getFrom();
        try {
            Class<?> travelAgentCls = Class.forName("org.bukkit.TravelAgent");
            Method getTravelAgent = event.getClass().getMethod("getPortalTravelAgent");
            Method findOrCreate = travelAgentCls.getMethod("findOrCreate", Location.class);
            Object travelAgent = getTravelAgent.invoke(event);

            if (location.getWorld().getEnvironment() == World.Environment.NETHER) {
                location.setWorld(gameWorld);
                location.setX(location.getX() * scale);
                location.setZ(location.getZ() * scale);
            } else {
                location.setWorld(netherWorld);
                location.setX(location.getX() / scale);
                location.setZ(location.getZ() / scale);
            }

            Location to = (Location) findOrCreate.invoke(travelAgent, location);
            ConditionUtils.notNull(to, "TravelAgent returned null location!");
            event.setTo(to);

        } catch (ReflectiveOperationException ex) {
            Log.warn("Unable to handle nether portal event", ex);
        }
    }
}
