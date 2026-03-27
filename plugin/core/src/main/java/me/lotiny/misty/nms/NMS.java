package me.lotiny.misty.nms;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import io.fairyproject.log.Log;
import io.fairyproject.mc.version.MCVersion;
import lombok.Getter;
import me.lotiny.misty.nms.helper.recipe.MistyShapedRecipe;
import me.lotiny.misty.nms.helper.recipe.MistyShapelessRecipe;
import me.lotiny.misty.utils.Utilities;
import me.lotiny.misty.utils.VersionUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public abstract class NMS {

    @Getter
    private static NMS instance;

    private static final NavigableMap<Integer, String> VERSION_MAP = new TreeMap<>();

    static {
        VERSION_MAP.put(10808, "1_8_8");
        VERSION_MAP.put(11202, "1_12_2");
        VERSION_MAP.put(11605, "1_16_5");
        VERSION_MAP.put(12100, "1_21");
        VERSION_MAP.put(12104, "1_21_4");
        VERSION_MAP.put(12111, "1_21_11");

        loadVersionedNms();
    }

    public boolean isItem(Material material) {
        throw new UnsupportedOperationException();
    }

    public XPotion getPotionEffect(ItemStack item) {
        throw new UnsupportedOperationException();
    }

    public int getPotionEffectLevel(ItemStack item) {
        throw new UnsupportedOperationException();
    }

    public void setGameRule(World world, String rule, Object value) {
        throw new UnsupportedOperationException();
    }

    public ItemStack getItemInHand(Player player) {
        throw new UnsupportedOperationException();
    }

    public void setItemInHand(Player player, ItemStack item) {
        throw new UnsupportedOperationException();
    }

    public ItemStack getItemInOffHand(Player player) {
        throw new UnsupportedOperationException();
    }

    public void setItemInOffHand(Player player, ItemStack item) {
        throw new UnsupportedOperationException();
    }

    public ShapedRecipe createShapedRecipe(MistyShapedRecipe recipe) {
        throw new UnsupportedOperationException();
    }

    public ShapelessRecipe createShapelessRecipe(MistyShapelessRecipe recipe) {
        throw new UnsupportedOperationException();
    }

    public InventoryView openWorkbench(Player player) {
        throw new UnsupportedOperationException();
    }

    public ItemStack createItemStack(XMaterial xMaterial, int amount) {
        throw new UnsupportedOperationException();
    }

    public ItemStack createItemStack(Block block, int amount) {
        throw new UnsupportedOperationException();
    }

    public double getMaxHealth(Player player) {
        throw new UnsupportedOperationException();
    }

    public void setMaxHealth(Player player, double health) {
        throw new UnsupportedOperationException();
    }

    public Objective registerHealthObjective(Scoreboard scoreboard) {
        throw new UnsupportedOperationException();
    }

    public void handleNetherPortal(PlayerPortalEvent event, World gameWorld, World netherWorld, int scale) {
        throw new UnsupportedOperationException();
    }

    private static void loadVersionedNms() {
        MCVersion version = VersionUtils.current();
        int major = version.getMajor();
        int minor = version.getMinor();
        int patch = version.getPatch();
        int currentWeight = (major * 10000) + (minor * 100) + patch;

        Map.Entry<Integer, String> entry = VERSION_MAP.floorEntry(currentWeight);

        if (entry == null || !isValidMatch(currentWeight, entry.getKey())) {
            Log.error("Misty does not support Minecraft " + major + "." + minor + "." + patch);
            Utilities.disable();
            return;
        }

        String classSuffix = entry.getValue();
        String className = "me.lotiny.misty.nms.v" + classSuffix + ".v" + classSuffix;
        try {
            instance = (NMS) Class.forName(className).getDeclaredConstructor().newInstance();
            Log.info("Loaded NMS v" + classSuffix + " from " + className);
        } catch (Exception e) {
            Log.error("Critical error loading NMS bridge for " + className, e);
            Utilities.disable();
        }
    }

    private static boolean isValidMatch(int currentWeight, int matchedKey) {
        int currentMinor = (currentWeight / 100) % 100;
        int matchedMinor = (matchedKey / 100) % 100;
        int currentPatch = currentWeight % 100;
        int matchedPatch = matchedKey % 100;

        if (currentMinor != matchedMinor) {
            return false;
        }

        if (currentMinor == 8 || currentMinor == 12 || currentMinor == 16) {
            return currentPatch == matchedPatch;
        }

        return currentPatch >= matchedPatch;
    }
}
