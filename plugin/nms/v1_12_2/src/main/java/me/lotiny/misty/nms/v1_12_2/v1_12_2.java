package me.lotiny.misty.nms.v1_12_2;

import com.cryptomorin.xseries.XPotion;
import me.lotiny.misty.shared.LegacyReflectionAdapter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public final class v1_12_2 extends LegacyReflectionAdapter {

    @Override
    public XPotion getPotionEffect(ItemStack item) {
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) return null;

        PotionType type = meta.getBasePotionData().getType();
        return type != null ? XPotion.of(type) : null;
    }
}
