package me.lotiny.misty.bukkit.utils;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import lombok.experimental.UtilityClass;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;

@UtilityClass
public class ItemStackUtils {

    public ItemStack of(XMaterial xMaterial) {
        return of(xMaterial, 1);
    }

    public ItemStack of(XMaterial xMaterial, int amount) {
        return ReflectionUtils.get().createItemStack(xMaterial, amount);
    }

    public ItemStack of(Block block) {
        return of(block, 1);
    }

    public ItemStack of(Block block, int amount) {
        return ReflectionUtils.get().createItemStack(block, amount);
    }

    @Contract("null -> true")
    public boolean isNull(ItemStack item) {
        return item == null || XMaterial.AIR.isSimilar(item) || item.getAmount() < 1;
    }

    public boolean isSimilar(ItemStack item1, ItemStack item2) {
        XMaterial xMat = XMaterial.matchXMaterial(item1);
        if (xMat != XMaterial.POTION) {
            return xMat.isSimilar(item2);
        }

        XPotion xPotion1 = ReflectionUtils.get().getPotionEffect(item1);
        XPotion xPotion2 = ReflectionUtils.get().getPotionEffect(item2);
        if (xPotion1 == null || xPotion2 == null) {
            return false;
        }

        return xPotion1 == xPotion2;
    }
}
