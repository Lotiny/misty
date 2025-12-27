package me.lotiny.misty.bukkit.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {

    public String rb(String string) {
        return string.replace(" ", "");
    }
}
