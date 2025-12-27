package me.lotiny.misty.nms.v1_21_4;

import me.lotiny.misty.shared.ReflectionAdapter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;

public class v1_21_4 extends ReflectionAdapter {

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public InventoryView openWorkbench(Player player) {
        return MenuType.CRAFTING.create(player, Component.text("Crafting"));
    }
}
