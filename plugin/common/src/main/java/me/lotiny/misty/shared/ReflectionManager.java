package me.lotiny.misty.shared;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import me.lotiny.misty.shared.recipe.MistyShapedRecipe;
import me.lotiny.misty.shared.recipe.MistyShapelessRecipe;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public interface ReflectionManager {

    boolean isItem(Material material);

    XPotion getPotionEffect(ItemStack item);

    int getPotionEffectLevel(ItemStack item);

    void setGameRule(World world, String rule, Object value);

    ItemStack getItemInHand(Player player);

    void setItemInHand(Player player, ItemStack item);

    ItemStack getItemInOffHand(Player player);

    void setItemInOffHand(Player player, ItemStack item);

    ShapedRecipe createShapedRecipe(MistyShapedRecipe recipe);

    ShapelessRecipe createShapelessRecipe(MistyShapelessRecipe recipe);

    InventoryView openWorkbench(Player player);

    ItemStack createItemStack(XMaterial xMaterial, int amount);

    ItemStack createItemStack(Block block, int amount);

    double getMaxHealth(Player player);

    void setMaxHealth(Player player, double health);

    Objective registerHealthObjective(Scoreboard scoreboard);

    void handleNetherPortal(PlayerPortalEvent event, World gameWorld, World netherWorld, int scale);
}
