package me.lotiny.misty.shared;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.util.ConditionUtils;
import io.fairyproject.util.Stacktrace;
import me.lotiny.misty.shared.recipe.MistyShapedRecipe;
import me.lotiny.misty.shared.recipe.MistyShapelessRecipe;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Method;

@SuppressWarnings({"deprecation", "removal"})
public class LegacyReflectionAdapter extends ReflectionAdapter {

    @Override
    public void setGameRule(World world, String rule, Object value) {
        try {
            Method method = world.getClass().getMethod("setGameRuleValue", String.class, String.class);
            method.setAccessible(true);
            method.invoke(world, rule, value.toString());
        } catch (Exception e) {
            Stacktrace.print(e);
        }
    }

    @Override
    public ShapedRecipe createShapedRecipe(MistyShapedRecipe recipe) {
        ShapedRecipe shapedRecipe = new ShapedRecipe(recipe.getResult());

        String shape = recipe.getShape();
        if (shape.length() != 9) {
            throw new IllegalArgumentException("Recipe shape must be a 9-character string for a 3x3 grid.");
        }
        shapedRecipe.shape(shape.substring(0, 3), shape.substring(3, 6), shape.substring(6));

        recipe.getIngredients().forEach((character, ingredientObject) -> {
            MaterialData ingredientData = parseIngredient(ingredientObject);
            shapedRecipe.setIngredient(character, ingredientData);
        });

        return shapedRecipe;
    }

    @Override
    public ShapelessRecipe createShapelessRecipe(MistyShapelessRecipe recipe) {
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(recipe.getResult());

        recipe.getIngredients().forEach((ingredientObject, count) -> {
            MaterialData ingredientData = parseIngredient(ingredientObject);
            shapelessRecipe.addIngredient(count, ingredientData);
        });

        return shapelessRecipe;
    }

    private MaterialData parseIngredient(Object ingredientObject) {
        Material material;
        byte data = 0;

        switch (ingredientObject) {
            case XMaterial xMat -> {
                material = xMat.get();
                data = xMat.getData();
            }
            case ItemStack is -> {
                material = is.getType();
                data = (byte) is.getDurability();
            }
            case MaterialData md -> {
                return md;
            }
            case Material mat -> material = mat;
            case null -> throw new IllegalArgumentException("Ingredient object cannot be null.");
            default ->
                    throw new IllegalArgumentException("Unsupported ingredient type: " + ingredientObject.getClass().getName());
        }

        ConditionUtils.notNull(material, "Ingredient material cannot be null. Input: " + ingredientObject);

        if (material.getMaxDurability() > 0) {
            return new MaterialData(material, (byte) -1);
        } else {
            return new MaterialData(material, data);
        }
    }

    @Override
    public ItemStack createItemStack(XMaterial xMaterial, int amount) {
        ConditionUtils.notNull(xMaterial.get(), "Material '" + xMaterial + "' not found");
        return new ItemStack(xMaterial.get(), amount, xMaterial.getData());
    }

    @Override
    public ItemStack createItemStack(Block block, int amount) {
        return new ItemStack(block.getType(), amount, block.getData());
    }

    @Override
    public Objective registerHealthObjective(Scoreboard scoreboard) {
        return scoreboard.registerNewObjective("showHealth", Criterias.HEALTH);
    }
}
