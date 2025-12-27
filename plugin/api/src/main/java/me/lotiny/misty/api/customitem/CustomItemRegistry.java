package me.lotiny.misty.api.customitem;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.List;
import java.util.Map;

public interface CustomItemRegistry {

    /**
     * Returns an unmodifiable view of all registered custom items.
     *
     * @return a map of custom item IDs to their {@link CustomItem} instances.
     */
    Map<String, CustomItem> getCustomItems();

    /**
     * Registers a new {@link CustomItem} into the registry.
     * <p>
     * This will also automatically register the custom item as a Bukkit event listener.
     *
     * @param customItem the custom item to register.
     */
    void register(CustomItem customItem);

    /**
     * Creates a recipe creator for a given {@link CustomItem}.
     * <p>
     * The recipe creator helps generate shaped or shapeless recipes
     * tied to this custom item.
     *
     * @param customItem the custom item.
     * @return a {@link CustomItemRecipeCreator} bound to the given item.
     */
    CustomItemRecipeCreator getCustomItemRecipeCreator(CustomItem customItem);

    /**
     * Creates an {@link ItemStack} result for the specified {@link CustomItem}.
     * <p>
     * The item stack will contain the custom NBT tag that identifies it as a custom item.
     *
     * @param customItem the custom item.
     * @return an {@link ItemStack} with the custom item tag applied.
     */
    ItemStack createResult(CustomItem customItem);

    /**
     * Adds a custom item tag to an existing {@link ItemStack}.
     * <p>
     * Useful when you want to transform a vanilla item into a tagged custom item instance.
     *
     * @param item       the base item.
     * @param customItem the custom item to tag the item with.
     * @return a new {@link ItemStack} with the tag applied.
     */
    ItemStack addCustomItemTag(ItemStack item, CustomItem customItem);

    /**
     * Retrieves the {@link CustomItem} definition of a tagged item.
     *
     * @param item the item stack to inspect.
     * @return the {@link CustomItem} if the item is custom, otherwise {@code null}.
     */
    CustomItem getCustomItem(ItemStack item);

    /**
     * Checks if the given {@link ItemStack} is a registered custom item.
     *
     * @param item the item stack to check.
     * @return {@code true} if the item is a custom item, otherwise {@code false}.
     */
    boolean isCustomItem(ItemStack item);

    /**
     * Checks if the given {@link ItemStack} matches the provided {@link CustomItem}.
     *
     * @param item   the item stack to check.
     * @param target the custom item definition to compare against.
     * @return {@code true} if the item matches the target custom item, otherwise {@code false}.
     */
    boolean isTargetCustomItem(ItemStack item, CustomItem target);

    /**
     * Finds all craftable recipes for a player based on their current inventory
     * and an optional used item.
     *
     * @param player   the player to check inventory for.
     * @param usedItem the item being considered as part of the craft (can be {@code null}).
     * @param update   whether to reduce the required amount by the used item count.
     * @return a list of {@link Recipe} objects that the player can craft.
     */
    List<Recipe> findCraftableRecipes(Player player, ItemStack usedItem, boolean update);

    /**
     * Checks whether a player can craft a specific recipe given their inventory and an optional used item.
     *
     * @param player   the player to check.
     * @param recipe   the recipe to validate.
     * @param usedItem the item being used in the crafting process (can be {@code null}).
     * @param update   whether to subtract the used item amount from the required materials.
     * @return {@code true} if the player can craft the recipe, otherwise {@code false}.
     */
    boolean isCanCraft(Player player, Recipe recipe, ItemStack usedItem, boolean update);
}
