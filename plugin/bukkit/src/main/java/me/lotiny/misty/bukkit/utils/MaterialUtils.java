package me.lotiny.misty.bukkit.utils;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.block.Block;

@UtilityClass
public class MaterialUtils {

    public boolean isFlower(Block block) {
        return match(block, XMaterial.POPPY)
                || match(block, XMaterial.BLUE_ORCHID)
                || match(block, XMaterial.ALLIUM)
                || match(block, XMaterial.AZURE_BLUET)
                || match(block, XMaterial.RED_TULIP)
                || match(block, XMaterial.ORANGE_TULIP)
                || match(block, XMaterial.WHITE_TULIP)
                || match(block, XMaterial.PINK_TULIP)
                || match(block, XMaterial.OXEYE_DAISY)
                || match(block, XMaterial.DANDELION)
                || match(block, XMaterial.CORNFLOWER)
                || match(block, XMaterial.SUNFLOWER)
                || match(block, XMaterial.LILAC)
                || match(block, XMaterial.ROSE_BUSH)
                || match(block, XMaterial.PEONY)
                || match(block, XMaterial.LILY_OF_THE_VALLEY)
                || match(block, XMaterial.TORCHFLOWER)
                || match(block, XMaterial.CLOSED_EYEBLOSSOM)
                || match(block, XMaterial.OPEN_EYEBLOSSOM)
                || match(block, XMaterial.WITHER_ROSE)
                || match(block, XMaterial.PITCHER_PLANT);
    }

    public boolean isDoublePlant(Block block) {
        return match(block, XMaterial.SUNFLOWER)
                || match(block, XMaterial.LILAC)
                || match(block, XMaterial.ROSE_BUSH)
                || match(block, XMaterial.PEONY)
                || match(block, XMaterial.TALL_GRASS)
                || match(block, XMaterial.LARGE_FERN)
                || match(block, XMaterial.PITCHER_PLANT);
    }

    public boolean isModern(XMaterial xMaterial) {
        return XTag.BANNERS.isTagged(xMaterial)
                || xMaterial == XMaterial.SLIME_BLOCK
                || xMaterial == XMaterial.WET_SPONGE
                || xMaterial == XMaterial.RED_SANDSTONE
                || xMaterial == XMaterial.RED_SANDSTONE_STAIRS
                || xMaterial == XMaterial.RED_SANDSTONE_SLAB
                || xMaterial == XMaterial.SMOOTH_RED_SANDSTONE
                || xMaterial == XMaterial.CHISELED_RED_SANDSTONE
                || xMaterial == XMaterial.ACTIVATOR_RAIL
                || xMaterial == XMaterial.BARRIER
                || xMaterial == XMaterial.ARMOR_STAND
                || xMaterial == XMaterial.RABBIT_FOOT
                || xMaterial == XMaterial.RABBIT_HIDE
                || xMaterial == XMaterial.RABBIT_STEW
                || xMaterial == XMaterial.RABBIT
                || xMaterial == XMaterial.COOKED_RABBIT
                || xMaterial == XMaterial.MUTTON
                || xMaterial == XMaterial.COOKED_MUTTON
                || xMaterial == XMaterial.PRISMARINE
                || xMaterial == XMaterial.PRISMARINE_SHARD
                || xMaterial == XMaterial.PRISMARINE_BRICKS
                || xMaterial == XMaterial.PRISMARINE_CRYSTALS
                || xMaterial == XMaterial.DARK_PRISMARINE
                || xMaterial == XMaterial.SEA_LANTERN
                || xMaterial == XMaterial.ACACIA_DOOR
                || xMaterial == XMaterial.BIRCH_DOOR
                || xMaterial == XMaterial.JUNGLE_DOOR
                || xMaterial == XMaterial.DARK_OAK_DOOR
                || xMaterial == XMaterial.SPRUCE_DOOR
                || xMaterial == XMaterial.IRON_TRAPDOOR
                || xMaterial == XMaterial.BIRCH_FENCE
                || xMaterial == XMaterial.JUNGLE_FENCE
                || xMaterial == XMaterial.DARK_OAK_FENCE
                || xMaterial == XMaterial.ACACIA_FENCE
                || xMaterial == XMaterial.SPRUCE_FENCE
                || xMaterial == XMaterial.JUNGLE_FENCE_GATE
                || xMaterial == XMaterial.BIRCH_FENCE_GATE
                || xMaterial == XMaterial.ACACIA_FENCE_GATE
                || xMaterial == XMaterial.DARK_OAK_FENCE_GATE
                || xMaterial == XMaterial.SPRUCE_FENCE_GATE
                || xMaterial == XMaterial.ENDERMITE_SPAWN_EGG
                || xMaterial == XMaterial.GUARDIAN_SPAWN_EGG
                || xMaterial == XMaterial.RABBIT_SPAWN_EGG
                || xMaterial == XMaterial.ANDESITE
                || xMaterial == XMaterial.DIORITE
                || xMaterial == XMaterial.GRANITE
                || xMaterial == XMaterial.POLISHED_ANDESITE
                || xMaterial == XMaterial.POLISHED_DIORITE
                || xMaterial == XMaterial.POLISHED_GRANITE;
    }

    public Material getMaterial(XMaterial xMaterial) {
        if (xMaterial.isSupported()) {
            return xMaterial.get();
        }

        return Material.REDSTONE_BLOCK;
    }

    public boolean match(Block block, XMaterial xMaterial) {
        if (!xMaterial.isSupported()) {
            return false;
        }

        return ItemStackUtils.of(block).isSimilar(ItemStackUtils.of(xMaterial));
    }

    public boolean isItem(Material material) {
        return ReflectionUtils.get().isItem(material);
    }
}
