package me.lotiny.misty.shared.utils;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bootstrap.bukkit.BukkitPlugin;
import io.fairyproject.util.ConditionUtils;
import lombok.experimental.UtilityClass;
import me.lotiny.misty.shared.recipe.MistyShapedRecipe;
import me.lotiny.misty.shared.recipe.MistyShapelessRecipe;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

@UtilityClass
public class RecipeChoiceUtils {

    public ShapedRecipe createShapedRecipe(MistyShapedRecipe recipe) {
        NamespacedKey key = new NamespacedKey(BukkitPlugin.INSTANCE, recipe.getNamespace());
        ShapedRecipe shapedRecipe = new ShapedRecipe(key, recipe.getResult());

        String shape = recipe.getShape();
        if (shape.length() != 9) {
            throw new IllegalArgumentException("Recipe shape must be a 9-character string for a 3x3 grid.");
        }
        shapedRecipe.shape(
                shape.substring(0, 3),
                shape.substring(3, 6),
                shape.substring(6)
        );

        recipe.getIngredients().forEach((character, ingredientObject) -> {
            RecipeChoice choice = createRecipeChoice(ingredientObject);
            shapedRecipe.setIngredient(character, choice);
        });

        return shapedRecipe;
    }

    public ShapelessRecipe createShapelessRecipe(MistyShapelessRecipe recipe) {
        NamespacedKey key = new NamespacedKey(BukkitPlugin.INSTANCE, recipe.getNamespace());
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(key, recipe.getResult());

        recipe.getIngredients().forEach((ingredientObject, count) -> {
            RecipeChoice choice = createRecipeChoice(ingredientObject);

            for (int i = 0; i < count; i++) {
                shapelessRecipe.addIngredient(choice);
            }
        });

        return shapelessRecipe;
    }

    private RecipeChoice createRecipeChoice(Object ingredientObject) {
        final ItemStack item;

        switch (ingredientObject) {
            case XMaterial xMat -> {
                item = xMat.parseItem();
                ConditionUtils.notNull(item, "Material '" + xMat + "' could not be parsed into an item.");
            }
            case ItemStack itemStack -> item = itemStack.clone();
            case Material material -> item = new ItemStack(material);
            case null -> throw new IllegalArgumentException("Ingredient object cannot be null.");
            default ->
                    throw new IllegalArgumentException("Unsupported ingredient type: " + ingredientObject.getClass().getName());
        }

        return new RecipeChoice.ExactChoice(item);
    }
}
