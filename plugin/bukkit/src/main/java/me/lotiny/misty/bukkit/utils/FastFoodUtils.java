package me.lotiny.misty.bukkit.utils;

import com.cryptomorin.xseries.XMaterial;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;

@UtilityClass
public class FastFoodUtils {

    public ItemStack of(XMaterial material, float time) {
        ItemStack item = ReflectionUtils.get().createItemStack(material, 1);
        return of(item, time);
    }

    @SuppressWarnings("UnstableApiUsage")
    public ItemStack of(ItemStack item, float time) {
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasFood()) {
            FoodComponent food = meta.getFood();
            food.setCanAlwaysEat(true);
            food.setNutrition(4);
            food.setSaturation(9.6F);
            meta.setFood(food);

            item.setItemMeta(meta);
        }

        item.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds(time)
                .build());

        return item;
    }
}
