package me.lotiny.misty.bukkit.utils.scenario;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.Fairy;
import io.fairyproject.container.Autowired;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.util.ConditionUtils;
import io.fairyproject.util.FastRandom;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.impl.ScenarioConfig;
import me.lotiny.misty.bukkit.hook.PluginHookManager;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import me.lotiny.misty.bukkit.utils.MaterialUtils;
import me.lotiny.misty.bukkit.utils.RandomSelector;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

@InjectableComponent
@RequiredArgsConstructor
public class FlowerPowerUtils {

    @Autowired
    private static FlowerPowerUtils instance;

    private final PluginHookManager pluginHookManager;
    private final GameManager gameManager;

    private ScenarioConfig config;

    @Getter
    private Set<ItemStack> items;
    @Getter
    private int dropXp;

    public static FlowerPowerUtils get() {
        return instance;
    }

    @PostInitialize
    public void onPostInit() {
        this.config = Config.getScenarioConfig();
        this.items = new HashSet<>();
        this.dropXp = config.getFlowerPower().getExpDrop();
        registerMaterials();
    }

    private void registerMaterials() {
        for (XMaterial xMaterial : XMaterial.values()) {
            if (xMaterial.isSupported() && MaterialUtils.isItem(xMaterial.get())) {
                if (pluginHookManager.isLegacySupport() && MaterialUtils.isModern(xMaterial)) continue;
                if (config.getFlowerPower().getBanned().contains(xMaterial)) continue;

                items.add(ItemStackUtils.of(xMaterial));
            }
        }
    }

    public ItemStack getRandomDrop() {
        FastRandom random = Fairy.random();
        RandomSelector<ItemStack> selector = new RandomSelector<>(items, random);
        ItemStack item = selector.getRandomElement();

        if (!gameManager.getGame().getSetting().isGodApple() && XMaterial.ENCHANTED_GOLDEN_APPLE.isSimilar(item)) {
            return getRandomDrop();
        }

        XMaterial xMaterial = XMaterial.matchXMaterial(item);
        ConditionUtils.notNull(xMaterial.get(), "Material '" + xMaterial + "' not found");
        int maxDrop = Math.min(config.getFlowerPower().getMaxDrop(), xMaterial.get().getMaxStackSize());
        return ItemStackUtils.of(xMaterial, random.nextInt(maxDrop) + 1);
    }
}
