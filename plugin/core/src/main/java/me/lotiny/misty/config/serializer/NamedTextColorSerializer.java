package me.lotiny.misty.config.serializer;

import de.exlll.configlib.Serializer;
import net.kyori.adventure.text.format.NamedTextColor;

public class NamedTextColorSerializer implements Serializer<NamedTextColor, String> {

    @Override
    public String serialize(NamedTextColor color) {
        return color.toString();
    }

    @Override
    public NamedTextColor deserialize(String input) {
        return NamedTextColor.NAMES.valueOr(input, NamedTextColor.RED);
    }
}
