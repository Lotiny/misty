package me.lotiny.misty.nms.v1_8_8;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import io.fairyproject.util.ConditionUtils;
import me.lotiny.misty.nms.NMS;
import me.lotiny.misty.nms.helper.recipe.MistyShapedRecipe;
import me.lotiny.misty.nms.helper.recipe.MistyShapelessRecipe;
import net.minecraft.server.v1_8_R3.Item;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.Potion;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

@SuppressWarnings("deprecation")
public class v1_8_8 extends NMS {

    @Override
    public boolean isItem(Material material) {
        return material != null && Item.getById(material.getId()) != null;
    }

    @Override
    public XPotion getPotionEffect(ItemStack item) {
        if (item.getType() != Material.POTION) {
            return null;
        }

        Potion potion = Potion.fromItemStack(item);
        return XPotion.of(potion.getType());
    }

    @Override
    public int getPotionEffectLevel(ItemStack item) {
        return Potion.fromItemStack(item).getLevel();
    }

    @Override
    public ItemStack getItemInHand(Player player) {
        return player.getItemInHand();
    }

    @Override
    public void setItemInHand(Player player, ItemStack item) {
        player.setItemInHand(item);
    }

    @Override
    public ItemStack getItemInOffHand(Player player) {
        return null;
    }

    @Override
    public void setItemInOffHand(Player player, ItemStack item) {}

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
                throw new IllegalArgumentException("Unsupported ingredient type: "
                        + ingredientObject.getClass().getName());
        }

        ConditionUtils.notNull(material, "Ingredient material cannot be null. Input: " + ingredientObject);

        if (material.getMaxDurability() > 0) {
            return new MaterialData(material, (byte) -1);
        } else {
            return new MaterialData(material, data);
        }
    }

    @Override
    public InventoryView openWorkbench(Player player) {
        return player.openWorkbench(player.getLocation(), true);
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
    public double getMaxHealth(Player player) {
        return player.getMaxHealth();
    }

    @Override
    public void setMaxHealth(Player player, double health) {
        player.setMaxHealth(health);
    }

    @Override
    public Objective registerHealthObjective(Scoreboard scoreboard) {
        return scoreboard.registerNewObjective("showHealth", Criterias.HEALTH);
    }

    @Override
    public void handleNetherPortal(PlayerPortalEvent event, World gameWorld, World netherWorld, int scale) {
        Location from = event.getFrom();
        TravelAgent travelAgent = event.getPortalTravelAgent();
        Location to = travelAgent.findOrCreate(from);

        event.setTo(to);
    }
}
