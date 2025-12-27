package me.lotiny.misty.bukkit.utils;

import com.cryptomorin.xseries.XAttribute;
import io.fairyproject.Fairy;
import io.fairyproject.util.ConditionUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.LootTables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LootTableHelper {

    @Getter
    private final List<LootTable> lootTables = new ArrayList<>();

    public LootTableHelper() {
        for (LootTables tables : LootTables.values()) {
            if (tables == null) continue;
            lootTables.add(tables.getLootTable());
        }
    }

    public Collection<ItemStack> getLootItems(Location location, Player player, int index) {
        LootContext.Builder builder = new LootContext.Builder(location);

        if (player != null) {
            builder.lootedEntity(player);

            Attribute attribute = XAttribute.LUCK.get();
            if (XAttribute.LUCK.isSupported() && attribute != null) {
                AttributeInstance instance = player.getAttribute(attribute);
                ConditionUtils.notNull(instance, "Could not get luck attribute instance for player " + player.getName());
                float playerLuck = (float) instance.getValue();

                builder.luck(playerLuck);
            }
        }

        return lootTables.get(index).populateLoot(Fairy.random(), builder.build());
    }
}
