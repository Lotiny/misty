package me.lotiny.misty.bukkit.utils;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import com.cryptomorin.xseries.profiles.PlayerProfiles;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.gameprofile.MojangGameProfile;
import com.cryptomorin.xseries.profiles.objects.ProfileInputType;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import io.fairyproject.Fairy;
import io.fairyproject.bootstrap.bukkit.BukkitPlugin;
import io.fairyproject.mc.scheduler.MCSchedulers;
import io.fairyproject.util.CC;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

@UtilityClass
public class Utilities {

    @SuppressWarnings("deprecation")
    public void broadcast(String message) {
        Bukkit.broadcastMessage(CC.translate(message));
    }

    public String getFormattedName(String string) {
        String[] words = string.split("_");
        StringBuilder formattedName = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                formattedName.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        return formattedName.toString().trim();
    }

    public void stop(long delayTicks) {
        MCSchedulers.getGlobalScheduler().schedule(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop"), delayTicks);
    }

    public void disable() {
        MCSchedulers.getGlobalScheduler().schedule(() -> Bukkit.getPluginManager().disablePlugin(BukkitPlugin.INSTANCE));
    }

    public int getFortuneDrop(XMaterial material, int fortuneLevel) {
        Random random = Fairy.random();

        if (XTag.REDSTONE_ORES.isTagged(material)) {
            int base = 4 + random.nextInt(2);
            if (fortuneLevel > 0) {
                base += random.nextInt(fortuneLevel + 2);
            }
            return base;
        }

        if (XTag.LAPIS_ORES.isTagged(material)) {
            int base = 4 + random.nextInt(5);
            if (fortuneLevel > 0) {
                base += random.nextInt(fortuneLevel + 2);
            }
            return base;
        }

        if (fortuneLevel <= 0) {
            return 1;
        }

        int extra = Math.max(0, random.nextInt(fortuneLevel + 2) - 1);
        return 1 + extra;
    }

    @SuppressWarnings("UnstableApiUsage")
    public ItemStack createSkull(String texture) {
        MojangGameProfile profile = PlayerProfiles.signXSeries(ProfileInputType.TEXTURE_URL.getProfile(texture));
        Profileable profileable = Profileable.of(profile.copy(), false);
        return XSkull.createItem().profile(profileable).apply();
    }
}
