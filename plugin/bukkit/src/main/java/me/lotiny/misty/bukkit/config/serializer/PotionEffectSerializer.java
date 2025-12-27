package me.lotiny.misty.bukkit.config.serializer;

import com.cryptomorin.xseries.XPotion;
import de.exlll.configlib.Serializer;
import org.bukkit.potion.PotionEffect;

import java.util.Optional;
import java.util.StringJoiner;


public class PotionEffectSerializer implements Serializer<PotionEffect, String> {

    @Override
    public String serialize(PotionEffect potionEffect) {
        StringJoiner joiner = new StringJoiner("|");
        joiner.add(XPotion.of(potionEffect.getType()).name());
        joiner.add(String.valueOf(potionEffect.getDuration()));
        joiner.add(String.valueOf(potionEffect.getAmplifier()));

        return joiner.toString();
    }

    @Override
    public PotionEffect deserialize(String string) {
        String[] serializedItems = string.split("\\|");
        if (serializedItems.length != 3) {
            return null;
        }

        Optional<XPotion> xPotionOpt = XPotion.of(serializedItems[0]);
        if (xPotionOpt.isPresent()) {
            XPotion xPotion = xPotionOpt.get();
            return xPotion.buildPotionEffect(Integer.parseInt(serializedItems[1]), Integer.parseInt(serializedItems[2]));
        }

        return null;
    }
}
