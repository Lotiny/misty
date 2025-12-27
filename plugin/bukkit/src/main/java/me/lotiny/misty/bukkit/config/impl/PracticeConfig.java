package me.lotiny.misty.bukkit.config.impl;

import com.cryptomorin.xseries.XMaterial;
import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.SerializeWith;
import lombok.Getter;
import lombok.Setter;
import me.lotiny.misty.bukkit.config.BaseConfig;
import me.lotiny.misty.bukkit.config.serializer.KitSerializer;
import me.lotiny.misty.bukkit.config.serializer.LocationSerializer;
import me.lotiny.misty.bukkit.kit.Kit;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@Setter
@Configuration
public final class PracticeConfig extends BaseConfig {

    @SerializeWith(serializer = LocationSerializer.class)
    @Comment("The center location of the practice area.")
    private Location location = new Location(Bukkit.getWorlds().getFirst(), 0, 100, 0);

    @Comment("If true, the practice will be enabled by default.")
    private boolean autoEnable = false;

    @Comment("Choose the maximum players that can join the practice.")
    private int maxPlayers = 50;

    @Comment("Choose the radius to teleport player in the area.")
    private int teleportRadius = 10;

    @SerializeWith(serializer = KitSerializer.class)
    @Comment("The kit for the player. (Use /practice setkit to set new loadout)")
    private Kit kit = new Kit(
            new ItemStack[]{
                    ItemStackUtils.of(XMaterial.DIAMOND_HELMET),
                    ItemStackUtils.of(XMaterial.IRON_CHESTPLATE),
                    ItemStackUtils.of(XMaterial.IRON_LEGGINGS),
                    ItemStackUtils.of(XMaterial.DIAMOND_BOOTS)
            },
            Arrays.copyOf(new ItemStack[]{
                    ItemStackUtils.of(XMaterial.IRON_SWORD),
                    ItemStackUtils.of(XMaterial.BOW),
                    ItemStackUtils.of(XMaterial.GOLDEN_APPLE, 3),
                    ItemStackUtils.of(XMaterial.ARROW, 4)
            }, 36)
    );
}
