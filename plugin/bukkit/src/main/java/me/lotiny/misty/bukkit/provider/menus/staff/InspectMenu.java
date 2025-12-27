package me.lotiny.misty.bukkit.provider.menus.staff;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import io.fairyproject.bukkit.gui.Gui;
import io.fairyproject.bukkit.gui.pane.NormalPane;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.bukkit.provider.menus.MenuItem;
import me.lotiny.misty.bukkit.provider.menus.MistyMenu;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.VersionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class InspectMenu extends MistyMenu {

    private static final int SLOT_HELMET = 36;
    private static final int SLOT_CHESTPLATE = 37;
    private static final int SLOT_LEGGINGS = 38;
    private static final int SLOT_BOOTS = 39;
    private static final int SLOT_OFFHAND = 40;
    private static final int SLOT_POTIONS = 41;
    private static final int SLOT_LEVEL = 42;
    private static final int SLOT_HEALTH = 43;
    private static final int SLOT_FOOD = 44;
    private final Player target;

    @Override
    public Component getTitle(Player player) {
        return Component.text(target.getName() + "'s inventory");
    }

    @Override
    public int getRows(Player player) {
        return 5;
    }

    @Override
    public boolean isFilled(Player player) {
        return false;
    }

    @Override
    public Map<Integer, MenuItem> getButtons(Player player, NormalPane pane, Gui gui) {
        Map<Integer, MenuItem> buttons = new HashMap<>();

        PlayerInventory inventory = target.getInventory();

        for (int i = 0; i < 36; i++) {
            putIfNotNull(buttons, i, inventory.getItem(i));
        }

        putIfNotNull(buttons, SLOT_HELMET, inventory.getHelmet());
        putIfNotNull(buttons, SLOT_CHESTPLATE, inventory.getChestplate());
        putIfNotNull(buttons, SLOT_LEGGINGS, inventory.getLeggings());
        putIfNotNull(buttons, SLOT_BOOTS, inventory.getBoots());

        if (VersionUtils.isHigher(9, 0)) {
            putIfNotNull(buttons, SLOT_OFFHAND, inventory.getItem(EquipmentSlot.OFF_HAND));
        }

        buttons.put(SLOT_POTIONS, MenuItem.of(
                ItemBuilder.of(XMaterial.BLAZE_POWDER)
                        .name("&aActive Potions")
                        .lore(
                                target.getActivePotionEffects().isEmpty()
                                        ? List.of("&cPlayer doesn't have any", "&cpotion effects")
                                        : target.getActivePotionEffects().stream()
                                        .map(effect -> XPotion.of(effect.getType()).friendlyName()
                                                + " " + (effect.getAmplifier() + 1)
                                                + " &7(&b" + formatDuration(effect.getDuration()) + "&7)")
                                        .collect(Collectors.toList())
                        )
                        .build()
        ));

        buttons.put(SLOT_LEVEL, MenuItem.of(
                ItemBuilder.of(target.getLevel() == 0 ? XMaterial.GLASS_BOTTLE : XMaterial.EXPERIENCE_BOTTLE)
                        .amount(getItemAmount(target.getLevel()))
                        .name("&bLevel &f" + target.getLevel())
                        .build()
        ));

        buttons.put(SLOT_HEALTH, MenuItem.of(
                ItemBuilder.of(XMaterial.GLISTERING_MELON_SLICE)
                        .amount(getItemAmount((int) Math.round(target.getHealth())))
                        .name(String.format("&aHealth &f%.1f/%.0f",
                                target.getHealth(),
                                PlayerUtils.getMaxHealth(target)))
                        .build()
        ));

        buttons.put(SLOT_FOOD, MenuItem.of(
                ItemBuilder.of(XMaterial.COOKED_BEEF)
                        .name("&6Food &f" + target.getFoodLevel() + "/20")
                        .lore("&7Saturation: &f" + target.getSaturation())
                        .build()
        ));

        return buttons;
    }

    private void putIfNotNull(Map<Integer, MenuItem> buttons, int slot, ItemStack item) {
        if (!ItemStackUtils.isNull(item)) {
            buttons.put(slot, MenuItem.of(item, false));
        }
    }

    private int getItemAmount(int value) {
        return Math.clamp(value, 1, 64);
    }

    private String formatDuration(int ticks) {
        int totalSeconds = ticks / 20;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
