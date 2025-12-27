package me.lotiny.misty.bukkit.utils;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.DependsOn;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.ConfigManager;
import me.lotiny.misty.bukkit.config.impl.MainConfig;
import org.bukkit.inventory.ItemStack;

@DependsOn(ConfigManager.class)
@InjectableComponent
public class GoldenHead {

    private static ItemStack item;

    public static ItemStack build() {
        return item;
    }

    @PostInitialize
    public void onPostInit() {
        MainConfig.Healing.HealingItem config = Config.getMainConfig().getHealing().getGoldenHead();
        XMaterial material = config.getMaterial();

        ItemBuilder builder;
        if (material == XMaterial.PLAYER_HEAD && config.getSkinUrl() != null) {
            builder = ItemBuilder.of(Utilities.createSkull(config.getSkinUrl()));
        } else {
            builder = ItemBuilder.of(material);
        }

        builder.name(config.getName());

        ItemStack result = builder.build();

        if (VersionUtils.isHigher(21, 3)) {
            result = FastFoodUtils.of(result, config.getTime());
        }

        item = result;

        if (item == null) {
            throw new NullPointerException("Failed to create a Golden Head");
        }
    }
}