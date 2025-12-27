package me.lotiny.misty.nms.v1_16_5;

import com.cryptomorin.xseries.XPotion;
import me.lotiny.misty.shared.ReflectionAdapter;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public final class v1_16_5 extends ReflectionAdapter {

    @Override
    public XPotion getPotionEffect(ItemStack item) {
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) return null;

        PotionType type = meta.getBasePotionData().getType();
        return XPotion.of(type);
    }

    @Override
    public Objective registerHealthObjective(Scoreboard scoreboard) {
        return scoreboard.registerNewObjective("showHealth", Criterias.HEALTH, Component.text("Health"));
    }
}
