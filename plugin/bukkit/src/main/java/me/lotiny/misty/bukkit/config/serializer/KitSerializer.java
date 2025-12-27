package me.lotiny.misty.bukkit.config.serializer;

import de.exlll.configlib.Serializer;
import io.fairyproject.bukkit.util.items.ItemUtil;
import me.lotiny.misty.bukkit.kit.Kit;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.StringJoiner;

public final class KitSerializer implements Serializer<Kit, Map<String, String>> {

    @Override
    public Map<String, String> serialize(Kit kit) {
        return Map.of(
                "armors", serialize(kit.getArmors()),
                "items", serialize(kit.getItems())
        );
    }

    @Override
    public Kit deserialize(Map<String, String> stringStringMap) {
        return new Kit(
                deserialize(stringStringMap.get("armors")),
                deserialize(stringStringMap.get("items"))
        );
    }

    private String serialize(ItemStack[] input) {
        StringJoiner joiner = new StringJoiner("|");
        for (ItemStack item : input) {
            joiner.add(ItemUtil.serializeItemStack(item));
        }

        return joiner.toString();
    }

    private ItemStack[] deserialize(String output) {
        String[] serializedItems = output.split("\\|");
        ItemStack[] items = new ItemStack[serializedItems.length];
        for (int i = 0; i < serializedItems.length; i++) {
            items[i] = ItemUtil.deserializeItemStack(serializedItems[i]);
        }
        return items;
    }
}
