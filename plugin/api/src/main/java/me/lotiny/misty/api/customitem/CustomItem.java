package me.lotiny.misty.api.customitem;

import lombok.Getter;
import me.lotiny.misty.api.MistyApi;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public abstract class CustomItem implements Listener {

    private final Map<UUID, Integer> playerCrafts = new HashMap<>();

    /**
     * @return the registry responsible for managing Misty custom items.
     */
    public CustomItemRegistry getCustomItemRegistry() {
        return MistyApi.getInstance().getCustomItemRegistry();
    }

    /**
     * @return a helper used to build shaped or shapeless recipes for this custom item.
     */
    public CustomItemRecipeCreator getRecipeCreator() {
        return getCustomItemRegistry().getCustomItemRecipeCreator(this);
    }

    /**
     * Checks whether the given item stack corresponds exactly to this custom item.
     *
     * @param item the item stack to evaluate
     * @return {@code true} if the stack represents this custom item
     */
    public boolean isTargetItem(ItemStack item) {
        return getCustomItemRegistry().isTargetCustomItem(item, this);
    }

    /**
     * @return the unique identifier used internally by Misty for this custom item.
     */
    public abstract String getId();

    /**
     * @return the display name shown to players in menus and messages.
     */
    public abstract String getName();

    /**
     * @return the base item that will be tagged and distributed when the craft completes.
     */
    public abstract ItemStack getItem();

    /**
     * @return the crafting limit applied per player.
     */
    public abstract CraftLimit getCraftLimit();

    /**
     * @return the list of Bukkit recipes this custom item introduces.
     */
    public abstract List<Recipe> getRecipes();

    /**
     * @return {@code true} when the crafting result should be removed immediately after crafting.
     */
    public boolean isRemoveResult() {
        return false;
    }
}
