package me.lotiny.misty.bukkit.provider.menus.user.recipes;

import io.fairyproject.bukkit.gui.Gui;
import io.fairyproject.bukkit.gui.pane.NormalPane;
import io.fairyproject.bukkit.gui.pane.PaginatedPane;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import io.fairyproject.util.CC;
import me.lotiny.misty.api.customitem.CustomItem;
import me.lotiny.misty.api.customitem.CustomItemRegistry;
import me.lotiny.misty.bukkit.provider.menus.MenuItem;
import me.lotiny.misty.bukkit.provider.menus.MistyPaginatedMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemRecipeMenu extends MistyPaginatedMenu {

    @Autowired
    private static CustomItemRegistry customItemRegistry;

    @Override
    public Component getTitle(Player player) {
        return Component.text("Recipes");
    }

    @Override
    public int getRows(Player player) {
        return 5;
    }

    @Override
    public List<MenuItem> getButtons(Player player, PaginatedPane pane, Gui menu) {
        return new ArrayList<>(customItemRegistry.getCustomItems().values())
                .stream()
                .map(customItem -> MenuItem.of(
                        buildIcon(customItem, player),
                        (clickedPlayer, clickType) -> {
                            playClick(clickedPlayer);
                            new RecipeMenu(customItem).open(player);
                        },
                        false
                ))
                .toList();
    }

    @Override
    public Map<Integer, MenuItem> getBorderButtons(Player player, NormalPane topPane, NormalPane bottomPane, Gui menu) {
        return Map.of();
    }

    @SuppressWarnings("deprecation")
    private ItemStack buildIcon(CustomItem customItem, Player player) {
        ItemStack icon = ItemBuilder.of(customItem.getItem())
                .name("&a" + customItem.getName())
                .build();

        ItemMeta meta = icon.getItemMeta();
        if (meta == null) return icon;

        List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.add(" ");
        lore.add(CC.translate("&7Crafted: &a" + getCraftedAmount(customItem, player) + "/" + customItem.getCraftLimit().getAmount()));

        meta.setLore(lore);
        icon.setItemMeta(meta);
        return icon;
    }

    private int getCraftedAmount(CustomItem customItem, Player player) {
        if (customItem.getCraftLimit().isUnique()) {
            return customItem.getPlayerCrafts().isEmpty() ? 0 : 1;
        }
        return customItem.getPlayerCrafts().getOrDefault(player.getUniqueId(), 0);
    }
}
