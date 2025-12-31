package me.lotiny.misty.bukkit.utils;

import com.cryptomorin.xseries.XMaterial;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.potion.PotionEffect;

import java.util.List;

@UtilityClass
public class FastFoodUtils {

    public ItemStack of(XMaterial material, float time, List<PotionEffect> effects) {
        ItemStack item = ReflectionUtils.get().createItemStack(material, 1);
        return of(item, time, effects);
    }

    @SuppressWarnings("UnstableApiUsage")
    public ItemStack of(ItemStack item, float time, List<PotionEffect> effects) {
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasFood()) {
            FoodComponent food = meta.getFood();
            food.setCanAlwaysEat(true);
            food.setNutrition(4);
            food.setSaturation(9.6F);
            meta.setFood(food);

            item.setItemMeta(meta);
        }

        Consumable.Builder consumable = Consumable.consumable();
        consumable.consumeSeconds(time);
        consumable.animation(ItemUseAnimation.EAT);
        consumable.hasConsumeParticles(true);
        consumable.addEffect(ConsumeEffect.applyStatusEffects(effects, 1));

        item.setData(DataComponentTypes.CONSUMABLE, consumable);

        return item;
    }
}
