package me.lotiny.misty.api.customitem;

import org.bukkit.inventory.Recipe;

import java.util.Map;

public interface CustomItemRecipeCreator {

    /**
     * Builds a shaped recipe for the associated custom item.
     *
     * @param shape       the shaped recipe configuration
     * @param ingredients the ingredient mapping
     * @return the resulting shaped recipe
     */
    Recipe createShaped(String shape, Map<Character, Object> ingredients);

    /**
     * Builds a shapeless recipe for the associated custom item.
     *
     * @param ingredients the ingredient mapping
     * @return the resulting shapeless recipe
     */
    Recipe createShapeless(Map<Object, Integer> ingredients);
}
