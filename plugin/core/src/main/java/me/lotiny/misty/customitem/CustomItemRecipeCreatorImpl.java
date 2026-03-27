package me.lotiny.misty.customitem;

import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.customitem.CustomItem;
import me.lotiny.misty.api.customitem.CustomItemRecipeCreator;
import me.lotiny.misty.api.customitem.CustomItemRegistry;
import me.lotiny.misty.nms.NMS;
import me.lotiny.misty.nms.helper.recipe.MistyShapedRecipe;
import me.lotiny.misty.nms.helper.recipe.MistyShapelessRecipe;
import org.bukkit.inventory.Recipe;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class CustomItemRecipeCreatorImpl implements CustomItemRecipeCreator {

    private final CustomItemRegistry customItemRegistry;
    private final CustomItem customItem;
    private final AtomicInteger recipeCounter = new AtomicInteger(0);

    @Override
    public Recipe createShaped(String shape, Map<Character, Object> ingredients) {
        if (shape.length() != 9) {
            throw new IllegalArgumentException("Invalid shape length: " + customItem.getId());
        }

        return NMS.getInstance()
                .createShapedRecipe(MistyShapedRecipe.builder()
                        .namespace(generateNamespace())
                        .result(customItemRegistry.createResult(customItem))
                        .shape(shape)
                        .ingredients(ingredients)
                        .build());
    }

    @Override
    public Recipe createShapeless(Map<Object, Integer> ingredients) {
        return NMS.getInstance()
                .createShapelessRecipe(MistyShapelessRecipe.builder()
                        .namespace(generateNamespace())
                        .result(customItemRegistry.createResult(customItem))
                        .ingredients(ingredients)
                        .build());
    }

    private String generateNamespace() {
        int count = recipeCounter.getAndIncrement();
        return count == 0 ? customItem.getId() : customItem.getId() + "-" + count;
    }
}
