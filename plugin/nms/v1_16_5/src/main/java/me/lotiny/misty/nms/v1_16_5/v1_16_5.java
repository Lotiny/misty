package me.lotiny.misty.nms.v1_16_5;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import io.fairyproject.bootstrap.bukkit.BukkitPlugin;
import io.fairyproject.util.ConditionUtils;
import me.lotiny.misty.nms.helper.recipe.MistyShapedRecipe;
import me.lotiny.misty.nms.helper.recipe.MistyShapelessRecipe;
import me.lotiny.misty.nms.v1_12_2.v1_12_2;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class v1_16_5 extends v1_12_2 {

    @Override
    public XPotion getPotionEffect(ItemStack item) {
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) return null;

        PotionType type = meta.getBasePotionData().getType();
        return XPotion.of(type);
    }

    @Override
    public ShapedRecipe createShapedRecipe(MistyShapedRecipe recipe) {
        NamespacedKey key = new NamespacedKey(BukkitPlugin.INSTANCE, recipe.getNamespace());
        ShapedRecipe shapedRecipe = new ShapedRecipe(key, recipe.getResult());

        String shape = recipe.getShape();
        if (shape.length() != 9) {
            throw new IllegalArgumentException("Recipe shape must be a 9-character string for a 3x3 grid.");
        }
        shapedRecipe.shape(shape.substring(0, 3), shape.substring(3, 6), shape.substring(6));

        recipe.getIngredients().forEach((character, ingredientObject) -> {
            RecipeChoice choice = createRecipeChoice(ingredientObject);
            shapedRecipe.setIngredient(character, choice);
        });

        return shapedRecipe;
    }

    @Override
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
                throw new IllegalArgumentException("Unsupported ingredient type: "
                        + ingredientObject.getClass().getName());
        }

        return new RecipeChoice.ExactChoice(item);
    }

    @Override
    public ItemStack createItemStack(XMaterial xMaterial, int amount) {
        ConditionUtils.notNull(xMaterial.get(), "Material '" + xMaterial + "' not found");
        return new ItemStack(xMaterial.get(), amount);
    }

    @Override
    public ItemStack createItemStack(Block block, int amount) {
        return new ItemStack(block.getBlockData().getMaterial(), amount);
    }

    @Override
    public Objective registerHealthObjective(Scoreboard scoreboard) {
        return scoreboard.registerNewObjective("showHealth", Criterias.HEALTH, "Health");
    }

    @Override
    public void handleNetherPortal(PlayerPortalEvent event, World gameWorld, World netherWorld, int scale) {
        Location location = event.getFrom();
        World world = location.getWorld();
        ConditionUtils.notNull(world, "World cannot be null in PlayerPortalEvent");
        if (world.getEnvironment() == World.Environment.NETHER) {
            location.setWorld(gameWorld);
            location.setX(location.getX() * scale);
            location.setZ(location.getZ() * scale);
            event.setTo(location);
        } else {
            location.setWorld(netherWorld);
            location.setX(location.getX() / scale);
            location.setZ(location.getZ() / scale);
            event.setTo(location);
        }
    }
}
