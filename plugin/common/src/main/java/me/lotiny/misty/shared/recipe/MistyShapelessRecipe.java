package me.lotiny.misty.shared.recipe;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@Getter
@Builder
public class MistyShapelessRecipe {

    private String namespace;
    private ItemStack result;
    private Map<Object, Integer> ingredients;

}
