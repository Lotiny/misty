package me.lotiny.misty.bukkit.provider.menus.user.recipes;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.gui.Gui;
import io.fairyproject.bukkit.gui.pane.NormalPane;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.customitem.CustomItem;
import me.lotiny.misty.bukkit.provider.menus.MenuItem;
import me.lotiny.misty.bukkit.provider.menus.MistyMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class RecipeMenu extends MistyMenu {

    private static final int[] CRAFTING_SLOTS = {11, 12, 13, 20, 21, 22, 29, 30, 31};
    private static final int SLOT_OUTPUT = 24;
    private static final int SLOT_SHAPELESS_INFO = 18;
    private static final int SLOT_BACK = 49;
    private final CustomItem customItem;

    @Override
    public Component getTitle(Player player) {
        return Component.text(customItem.getName());
    }

    @Override
    public int getRows(Player player) {
        return 6;
    }

    @Override
    public Map<Integer, MenuItem> getButtons(Player player, NormalPane pane, Gui gui) {
        Map<Integer, MenuItem> slots = new HashMap<>();

        List<Recipe> recipes = customItem.getRecipes();
        if (recipes.isEmpty()) {
            slots.put(SLOT_OUTPUT, MenuItem.of(
                    ItemBuilder.of(XMaterial.BARRIER)
                            .name("&cNo Recipe Found")
                            .lore("&7This item has no registered recipe.")
                            .build()
            ));
        } else {
            Recipe recipe = recipes.getFirst();
            if (recipe instanceof ShapedRecipe shaped) {
                handleShapedRecipe(slots, shaped);
            } else if (recipe instanceof ShapelessRecipe shapeless) {
                handleShapelessRecipe(slots, shapeless);
            }
        }

        slots.put(SLOT_OUTPUT, MenuItem.of(customItem.getItem(), false));

        slots.put(SLOT_BACK, MenuItem.of(
                ItemBuilder.of(XMaterial.REDSTONE)
                        .name("&cBack")
                        .build(),
                (clickedPlayer, clickType) -> {
                    playClick(clickedPlayer);
                    new ItemRecipeMenu().open(clickedPlayer);
                }
        ));

        return slots;
    }

    private void handleShapedRecipe(Map<Integer, MenuItem> slots, ShapedRecipe recipe) {
        int index = 0;
        for (String row : recipe.getShape()) {
            for (char c : row.toCharArray()) {
                ItemStack ingredient = recipe.getIngredientMap().get(c);
                slots.put(CRAFTING_SLOTS[index], MenuItem.of(ingredient != null ? ingredient : XMaterial.AIR.parseItem(), (player, clickType) -> player.getInventory().addItem(ingredient), false));
                index++;
            }
        }
    }

    private void handleShapelessRecipe(Map<Integer, MenuItem> slots, ShapelessRecipe recipe) {
        int index = 0;
        for (ItemStack ingredient : recipe.getIngredientList()) {
            int amount = Math.max(1, ingredient.getAmount());
            for (int i = 0; i < amount && index < CRAFTING_SLOTS.length; i++, index++) {
                slots.put(CRAFTING_SLOTS[index], MenuItem.of(ingredient, (player, clickType) -> player.getInventory().addItem(ingredient), false));
            }
        }

        while (index < CRAFTING_SLOTS.length) {
            slots.put(CRAFTING_SLOTS[index], MenuItem.of(XMaterial.AIR.parseItem()));
            index++;
        }

        slots.put(SLOT_SHAPELESS_INFO, MenuItem.of(
                ItemBuilder.of(XMaterial.REDSTONE_TORCH)
                        .name("&cShapeless Recipe")
                        .enchantment(XEnchantment.UNBREAKING, 1)
                        .lore(
                                "&7This item is a shapeless recipe,",
                                "&7meaning you can craft it",
                                "&7in any shape."
                        )
                        .build()
        ));
    }
}
