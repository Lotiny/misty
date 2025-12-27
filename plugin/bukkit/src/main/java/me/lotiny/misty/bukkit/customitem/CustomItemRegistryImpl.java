package me.lotiny.misty.bukkit.customitem;

import io.fairyproject.bootstrap.bukkit.BukkitPlugin;
import io.fairyproject.bukkit.nbt.NBTKey;
import io.fairyproject.bukkit.nbt.NBTModifier;
import io.fairyproject.bukkit.nbt.impl.NBTModifierNBTAPI;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import me.lotiny.misty.api.customitem.CustomItem;
import me.lotiny.misty.api.customitem.CustomItemRecipeCreator;
import me.lotiny.misty.api.customitem.CustomItemRegistry;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.*;

@InjectableComponent
public class CustomItemRegistryImpl implements CustomItemRegistry {

    private Map<String, CustomItem> customItems;
    private NBTModifier nbt;
    private NBTKey customItemKey;

    @PostInitialize
    public void onPostInit() {
        customItems = new LinkedHashMap<>();
        nbt = new NBTModifierNBTAPI();
        customItemKey = NBTKey.create("misty:custom-item");
    }

    @Override
    public Map<String, CustomItem> getCustomItems() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(customItems));
    }

    @Override
    public void register(CustomItem customItem) {
        customItems.put(customItem.getId(), customItem);
        Bukkit.getPluginManager().registerEvents(customItem, BukkitPlugin.INSTANCE);
    }

    @Override
    public CustomItemRecipeCreator getCustomItemRecipeCreator(CustomItem customItem) {
        return new CustomItemRecipeCreatorImpl(this, customItem);
    }

    @Override
    public ItemStack createResult(CustomItem customItem) {
        return ItemBuilder.of(customItem.getItem())
                .tag(customItemKey, customItem.getId())
                .build();
    }

    @Override
    public ItemStack addCustomItemTag(ItemStack item, CustomItem customItem) {
        return ItemBuilder.of(item)
                .tag(customItemKey, customItem.getId())
                .build();
    }

    @Override
    public CustomItem getCustomItem(ItemStack item) {
        if (!isCustomItem(item)) {
            return null;
        }

        String id = nbt.getString(item, customItemKey);
        return customItems.get(id);
    }

    @Override
    public boolean isCustomItem(ItemStack item) {
        if (ItemStackUtils.isNull(item)) {
            return false;
        }
        return nbt.has(item, customItemKey);
    }

    @Override
    public boolean isTargetCustomItem(ItemStack item, CustomItem target) {
        if (target == null) {
            return false;
        }

        CustomItem found = getCustomItem(item);
        return found != null && found == target;
    }

    @Override
    public List<Recipe> findCraftableRecipes(Player player, ItemStack usedItem, boolean update) {
        List<Recipe> recipes = new ArrayList<>();

        for (CustomItem customItem : this.customItems.values()) {
            for (Recipe recipe : customItem.getRecipes()) {
                if (isCanCraft(player, recipe, usedItem, update)) {
                    recipes.add(recipe);
                }
            }
        }

        return recipes;
    }

    @Override
    public boolean isCanCraft(Player player, Recipe recipe, ItemStack usedItem, boolean update) {
        if (recipe instanceof ShapedRecipe) {
            return canCraftShaped(player, (ShapedRecipe) recipe, usedItem, update);
        } else if (recipe instanceof ShapelessRecipe) {
            return canCraftShapeless(player, (ShapelessRecipe) recipe, usedItem, update);
        }
        return false;
    }

    private boolean canCraftShaped(Player player, ShapedRecipe recipe, ItemStack usedItem, boolean update) {
        Map<Character, Integer> ingredientCounts = new HashMap<>();
        for (String row : recipe.getShape()) {
            for (char c : row.toCharArray()) {
                ingredientCounts.put(c, ingredientCounts.getOrDefault(c, 0) + 1);
            }
        }

        for (Map.Entry<Character, ItemStack> entry : recipe.getIngredientMap().entrySet()) {
            char key = entry.getKey();
            ItemStack requiredItem = entry.getValue();
            if (requiredItem == null) {
                continue;
            }

            int requiredAmount = ingredientCounts.getOrDefault(key, 0);

            if (usedItem != null && ItemStackUtils.isSimilar(requiredItem, usedItem)) {
                if (isCustomItem(usedItem)) {
                    continue;
                }

                if (update) {
                    requiredAmount -= usedItem.getAmount();
                    if (requiredAmount <= 0) {
                        continue;
                    }
                }
            }

            int foundAmount = countMaterialInInventory(player, requiredItem);
            if (foundAmount < requiredAmount) {
                return false;
            }
        }

        return true;
    }

    private boolean canCraftShapeless(Player player, ShapelessRecipe recipe, ItemStack usedItem, boolean update) {
        Map<ItemStack, Integer> requiredMap = new HashMap<>();
        for (ItemStack ingredient : recipe.getIngredientList()) {
            if (ingredient == null) continue;

            requiredMap.merge(ingredient, ingredient.getAmount(), Integer::sum);
        }

        for (Map.Entry<ItemStack, Integer> entry : requiredMap.entrySet()) {
            ItemStack item = entry.getKey();
            int requiredAmount = entry.getValue();

            if (usedItem != null && ItemStackUtils.isSimilar(item, usedItem)) {
                if (!isCustomItem(usedItem) && update) {
                    requiredAmount -= usedItem.getAmount();
                    if (requiredAmount <= 0) {
                        continue;
                    }
                }
            }

            int foundAmount = countMaterialInInventory(player, item);
            if (foundAmount < requiredAmount) {
                return false;
            }
        }

        return true;
    }

    private int countMaterialInInventory(Player player, ItemStack item) {
        int foundAmount = 0;
        for (ItemStack invItem : player.getInventory().getContents()) {
            if (invItem == null || isCustomItem(invItem)) {
                continue;
            }

            if (ItemStackUtils.isSimilar(item, invItem)) {
                foundAmount += invItem.getAmount();
            }
        }
        return foundAmount;
    }
}
