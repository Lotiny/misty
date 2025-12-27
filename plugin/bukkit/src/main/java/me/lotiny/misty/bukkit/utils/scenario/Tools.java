package me.lotiny.misty.bukkit.utils.scenario;

import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class Tools {

    private static final Map<XMaterial, Boolean> MAP = new HashMap<>();

    static {
        MAP.put(XMaterial.NETHERITE_SWORD, false);
        MAP.put(XMaterial.DIAMOND_SWORD, false);
        MAP.put(XMaterial.GOLDEN_SWORD, false);
        MAP.put(XMaterial.IRON_SWORD, false);
        MAP.put(XMaterial.STONE_SWORD, false);
        MAP.put(XMaterial.WOODEN_SWORD, false);

        for (XMaterial xMaterial : new XMaterial[]{
                XMaterial.NETHERITE_PICKAXE, XMaterial.NETHERITE_AXE, XMaterial.NETHERITE_SHOVEL, XMaterial.NETHERITE_HOE,
                XMaterial.DIAMOND_PICKAXE, XMaterial.DIAMOND_AXE, XMaterial.DIAMOND_SHOVEL, XMaterial.DIAMOND_HOE,
                XMaterial.GOLDEN_PICKAXE, XMaterial.GOLDEN_AXE, XMaterial.GOLDEN_SHOVEL, XMaterial.GOLDEN_HOE,
                XMaterial.IRON_PICKAXE, XMaterial.IRON_AXE, XMaterial.IRON_SHOVEL, XMaterial.IRON_HOE,
                XMaterial.STONE_PICKAXE, XMaterial.STONE_AXE, XMaterial.STONE_SHOVEL, XMaterial.STONE_HOE,
                XMaterial.WOODEN_PICKAXE, XMaterial.WOODEN_AXE, XMaterial.WOODEN_SHOVEL, XMaterial.WOODEN_HOE
        }) {
            MAP.put(xMaterial, true);
        }
    }

    public boolean isTool(XMaterial xMaterial) {
        return isTool(xMaterial, false);
    }

    public boolean isTool(XMaterial xMaterial, boolean includeSword) {
        return includeSword || MAP.getOrDefault(xMaterial, false);
    }
}
