package me.lotiny.misty.bukkit.utils;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.util.SpigotUtil;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.DependsOn;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.util.ConditionUtils;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.ConfigManager;
import me.lotiny.misty.paper.MistyPaper;
import org.bukkit.inventory.ItemStack;

@DependsOn(ConfigManager.class)
@InjectableComponent
public class GoldenHead {

    private static ItemStack cachedItem;

    public static ItemStack getItem() {
        return cachedItem.clone();
    }

    @PostInitialize
    public void onPostInit() {
        var config = Config.getMainConfig().getHealing().getGoldenHead();
        XMaterial material = config.getMaterial();

        ItemBuilder builder;
        if (material == XMaterial.PLAYER_HEAD && config.getSkinUrl() != null) {
            builder = ItemBuilder.of(Utilities.createSkull(config.getSkinUrl()));
        } else {
            builder = ItemBuilder.of(material);
        }

        ItemStack tempItem = builder.name(config.getName()).build();

        ConditionUtils.notNull(tempItem, "Failed to create a GoldenHead");

        if (VersionUtils.isHigher(21, 4) && SpigotUtil.SPIGOT_TYPE == SpigotUtil.SpigotType.PAPER) {
            MistyPaper.applyConsumable(tempItem, config.getTime(), config.getPotionEffects());
        }

        cachedItem = tempItem;
    }
}
