package me.lotiny.misty.bukkit.utils.scenario;

import com.cryptomorin.xseries.XEntityType;
import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.container.Autowired;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.util.ConditionUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.bukkit.hook.PluginHookManager;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import me.lotiny.misty.bukkit.utils.LootTableHelper;
import me.lotiny.misty.bukkit.utils.MaterialUtils;
import me.lotiny.misty.bukkit.utils.VersionUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@InjectableComponent
@RequiredArgsConstructor
public class RandomizerUtils {

    @Autowired
    private static RandomizerUtils instance;

    private final PluginHookManager pluginHookManager;

    private Set<ItemStack> available;
    private Map<XMaterial, Object> partneredBlock;
    private Map<EntityType, XMaterial> partneredEntity;

    @Getter
    private LootTableHelper lootTableHelper;

    public static RandomizerUtils get() {
        return instance;
    }

    @PostInitialize
    public void onPostInit() {
        available = new HashSet<>();
        partneredBlock = new ConcurrentHashMap<>();
        partneredEntity = new ConcurrentHashMap<>();
        registerPartner();
    }

    private void registerPartner() {
        for (XMaterial xMaterial : XMaterial.values()) {
            if (xMaterial.isSupported() && MaterialUtils.isItem(xMaterial.get())) {
                if (pluginHookManager.isLegacySupport() && MaterialUtils.isModern(xMaterial)) {
                    continue;
                }

                available.add(ItemStackUtils.of(xMaterial));
            }
        }

        List<ItemStack> remaining = new ArrayList<>();
        List<XMaterial> materials = new ArrayList<>(Arrays.asList(XMaterial.values()));
        Collections.shuffle(materials);

        if (VersionUtils.isHigher(16, 5)) {
            lootTableHelper = new LootTableHelper();
            for (int i = 0; i < lootTableHelper.getLootTables().size(); i++) {
                XMaterial xMaterial = materials.removeFirst();

                if (!xMaterial.isSupported() || pluginHookManager.isLegacySupport() && MaterialUtils.isModern(xMaterial))
                    continue;

                Material material = xMaterial.get();
                if (material == null || !material.isBlock()) continue;

                partneredBlock.put(xMaterial, i);
            }
        }

        for (XMaterial xMaterial : materials) {
            if (remaining.isEmpty()) {
                remaining.addAll(available);
                Collections.shuffle(remaining);
            }

            if (!xMaterial.isSupported() || pluginHookManager.isLegacySupport() && MaterialUtils.isModern(xMaterial))
                continue;

            Material material = xMaterial.get();
            if (material == null || !material.isBlock()) continue;

            ItemStack item = remaining.removeFirst();
            partneredBlock.put(xMaterial, XMaterial.matchXMaterial(item));
        }

        for (XEntityType xEntityType : XEntityType.values()) {
            if (remaining.isEmpty()) {
                remaining.addAll(available);
                Collections.shuffle(remaining);
            }

            if (!xEntityType.isSupported() || xEntityType == XEntityType.PLAYER) {
                continue;
            }

            ItemStack item = remaining.removeFirst();
            partneredEntity.put(xEntityType.get(), XMaterial.matchXMaterial(item));
        }
    }

    public Object getDropItem(Block block) {
        return partneredBlock.get(XMaterial.matchXMaterial(block.getType()));
    }

    public ItemStack getDropItem(EntityType entityType) {
        XMaterial xMaterial = partneredEntity.get(entityType);
        Material material = xMaterial.get();
        ConditionUtils.notNull(material, "Failed to find a Partnered item with " + entityType.name());

        return ItemStackUtils.of(xMaterial);
    }
}
