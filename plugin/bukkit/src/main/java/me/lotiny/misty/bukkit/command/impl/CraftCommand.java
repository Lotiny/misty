package me.lotiny.misty.bukkit.command.impl;

import com.cryptomorin.xseries.inventory.XInventoryView;
import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.command.annotation.Arg;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.mc.scheduler.MCSchedulers;
import io.fairyproject.util.CC;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.customitem.CustomItem;
import me.lotiny.misty.api.customitem.CustomItemRegistry;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import me.lotiny.misty.bukkit.utils.ReflectionUtils;
import me.lotiny.misty.bukkit.utils.VersionUtils;
import org.bukkit.inventory.*;

import java.util.List;
import java.util.Map;

@InjectableComponent
@RequiredArgsConstructor
@Command({"craft", "itemcraft", "craftitem"})
public class CraftCommand extends AbstractCommand {

    private final CustomItemRegistry customItemRegistry;

    @Command("#")
    public void onCommand(BukkitCommandContext context, @Arg("item") CustomItem craft) {
        mustBePlayer(context, player -> {
            Recipe foundRecipe = null;
            for (Recipe recipe : craft.getRecipes()) {
                if (customItemRegistry.isCanCraft(player, recipe, null, false)) {
                    foundRecipe = recipe;
                    break;
                }
            }

            if (foundRecipe == null) {
                player.sendMessage(CC.translate("&cYou don't have enough item to craft this item."));
                return;
            }

            InventoryView view = ReflectionUtils.get().openWorkbench(player);
            if (VersionUtils.isHigher(21, 4)) {
                player.openInventory(view);
            }

            Inventory inventory = XInventoryView.of(view).getTopInventory();
            ItemStack[] contents = player.getInventory().getContents();
            if (foundRecipe instanceof ShapedRecipe shapedRecipe) {
                Map<Character, ItemStack> ingredientMap = shapedRecipe.getIngredientMap();
                String[] shape = shapedRecipe.getShape();

                for (int i = 0; i < shape.length; i++) {
                    String row = shape[i];

                    for (int j = 0; j < row.length(); j++) {
                        char key = row.charAt(j);
                        ItemStack required = ingredientMap.get(key);
                        if (ItemStackUtils.isNull(required)) {
                            continue;
                        }

                        int craftingSlot = i * 3 + j + 1;

                        for (int k = 0; k < contents.length; k++) {
                            ItemStack content = contents[k];
                            if (content == null) {
                                continue;
                            }

                            if (ItemStackUtils.isSimilar(content, required) && !customItemRegistry.isCustomItem(content)) {
                                ItemStack input = content.clone();
                                input.setAmount(1);
                                inventory.setItem(craftingSlot, input);

                                if (content.getAmount() == 1) {
                                    player.getInventory().setItem(k, null);
                                } else {
                                    content.setAmount(content.getAmount() - 1);
                                }
                                break;
                            }
                        }
                    }
                }

            } else if (foundRecipe instanceof ShapelessRecipe shapelessRecipe) {
                List<ItemStack> ingredients = shapelessRecipe.getIngredientList();
                int craftingSlot = 1;

                for (ItemStack ingredient : ingredients) {
                    if (ItemStackUtils.isNull(ingredient)) {
                        continue;
                    }

                    int requiredAmount = Math.max(1, ingredient.getAmount());
                    for (int i = 0; i < requiredAmount; i++) {
                        for (int j = 0; j < contents.length; j++) {
                            ItemStack content = contents[j];
                            if (content == null) {
                                continue;
                            }

                            if (ItemStackUtils.isSimilar(content, ingredient) && !customItemRegistry.isCustomItem(content)) {
                                ItemStack input = content.clone();
                                input.setAmount(1);
                                inventory.setItem(craftingSlot++, input);

                                if (content.getAmount() == 1) {
                                    player.getInventory().setItem(j, null);
                                } else {
                                    content.setAmount(content.getAmount() - 1);
                                }
                                break;
                            }
                        }
                    }
                }
            }

            MCSchedulers.getGlobalScheduler().schedule(player::updateInventory, 5L);
        });
    }
}
