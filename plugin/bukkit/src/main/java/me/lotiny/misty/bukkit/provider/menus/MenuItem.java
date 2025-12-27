package me.lotiny.misty.bukkit.provider.menus;

import com.cryptomorin.xseries.XAttribute;
import com.cryptomorin.xseries.XItemFlag;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import io.fairyproject.bukkit.gui.slot.GuiSlot;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import me.lotiny.misty.bukkit.utils.VersionUtils;
import me.lotiny.misty.bukkit.utils.scenario.Tools;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.BiConsumer;

@Getter
@Setter
public class MenuItem {

    private ItemStack item;
    private BiConsumer<Player, ClickType> callback;
    private boolean hideAttributes;

    public MenuItem(ItemStack item, boolean hideAttributes) {
        this.item = item;
        this.hideAttributes = hideAttributes;
    }

    public MenuItem(ItemStack item, BiConsumer<Player, ClickType> callback, boolean hideAttributes) {
        this.item = item;
        this.callback = callback;
        this.hideAttributes = hideAttributes;
    }

    public static MenuItem of(ItemStack item) {
        return new MenuItem(item, true);
    }

    public static MenuItem of(ItemStack item, boolean hideAttributes) {
        return new MenuItem(item, hideAttributes);
    }

    public static MenuItem of(ItemStack item, BiConsumer<Player, ClickType> callback) {
        return new MenuItem(item, callback, true);
    }

    public static MenuItem of(ItemStack item, BiConsumer<Player, ClickType> callback, boolean hideAttributes) {
        return new MenuItem(item, callback, hideAttributes);
    }

    public GuiSlot build() {
        if (hideAttributes) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                XMaterial xMaterial = XMaterial.matchXMaterial(item);
                if (XTag.ARMOR_PIECES.isTagged(xMaterial) || Tools.isTool(xMaterial, true)) {
                    handleItemAttribute(meta);
                }
                XItemFlag.decorationOnly(meta);
                item.setItemMeta(meta);
            }
        }

        short maxDurability = item.getType().getMaxDurability();
        if (maxDurability > 0) {
            item = ItemBuilder.of(item)
                    .durability(maxDurability)
                    .build();
        }

        if (callback == null) {
            return GuiSlot.of(item);
        } else {
            return GuiSlot.of(item, callback);
        }
    }

    private void handleItemAttribute(ItemMeta meta) {
        if (!VersionUtils.isHigher(21, 0)) return;
        XAttribute xAttribute = XAttribute.ATTACK_DAMAGE;
        if (xAttribute.isSupported() && xAttribute.get() != null) {
            meta.addAttributeModifier(
                    xAttribute.get(),
                    XAttribute.createModifier("attack-damage", 10, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND)
            );
        }
    }
}
