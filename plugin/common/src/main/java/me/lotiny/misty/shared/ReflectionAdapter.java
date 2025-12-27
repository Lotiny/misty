package me.lotiny.misty.shared;

import com.cryptomorin.xseries.XAttribute;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import io.fairyproject.util.ConditionUtils;
import me.lotiny.misty.shared.recipe.MistyShapedRecipe;
import me.lotiny.misty.shared.recipe.MistyShapelessRecipe;
import me.lotiny.misty.shared.utils.RecipeChoiceUtils;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

public class ReflectionAdapter implements ReflectionManager {

    @Override
    public boolean isItem(Material material) {
        return material.isItem();
    }

    @Override
    public XPotion getPotionEffect(ItemStack item) {
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) return null;

        PotionType type = meta.getBasePotionType();
        return type != null ? XPotion.of(type) : null;
    }

    @Override
    public int getPotionEffectLevel(ItemStack item) {
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) return 1;

        if (meta.hasCustomEffects() && !meta.getCustomEffects().isEmpty()) {
            PotionEffect first = meta.getCustomEffects().getFirst();
            return first.getAmplifier() + 1;
        }

        return 1;
    }

    @SuppressWarnings("removal")
    @Override
    public void setGameRule(World world, String rule, Object value) {
        GameRule<?> gameRule = GameRule.getByName(rule);
        me.lotiny.misty.shared.utils.GameRule.setGameRule(gameRule, world, rule, value);
    }

    @Override
    public ItemStack getItemInHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }

    @Override
    public void setItemInHand(Player player, ItemStack item) {
        player.getInventory().setItemInMainHand(item);
    }

    @Override
    public ItemStack getItemInOffHand(Player player) {
        return player.getInventory().getItemInOffHand();
    }

    @Override
    public void setItemInOffHand(Player player, ItemStack item) {
        player.getInventory().setItemInOffHand(item);
    }

    @Override
    public ShapedRecipe createShapedRecipe(MistyShapedRecipe recipe) {
        return RecipeChoiceUtils.createShapedRecipe(recipe);
    }

    @Override
    public ShapelessRecipe createShapelessRecipe(MistyShapelessRecipe recipe) {
        return RecipeChoiceUtils.createShapelessRecipe(recipe);
    }

    @SuppressWarnings("deprecation")
    @Override
    public InventoryView openWorkbench(Player player) {
        return player.openWorkbench(player.getLocation(), true);
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
    public double getMaxHealth(Player player) {
        Attribute attribute = XAttribute.MAX_HEALTH.get();
        ConditionUtils.notNull(attribute, "Cannot get Attribute MAX_HEALTH from player");

        AttributeInstance instance = player.getAttribute(attribute);
        ConditionUtils.notNull(instance, "Cannot get AttributeInstance MAX_HEALTH from player");
        return instance.getValue();
    }

    @Override
    public void setMaxHealth(Player player, double health) {
        Attribute attribute = XAttribute.MAX_HEALTH.get();
        ConditionUtils.notNull(attribute, "Cannot get Attribute MAX_HEALTH from player");

        AttributeInstance instance = player.getAttribute(attribute);
        ConditionUtils.notNull(instance, "Cannot get AttributeInstance MAX_HEALTH from player");
        instance.setBaseValue(health);
    }

    @Override
    public Objective registerHealthObjective(Scoreboard scoreboard) {
        //noinspection deprecation
        return scoreboard.registerNewObjective("showHealth", Criteria.HEALTH, "Health", RenderType.HEARTS);
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
