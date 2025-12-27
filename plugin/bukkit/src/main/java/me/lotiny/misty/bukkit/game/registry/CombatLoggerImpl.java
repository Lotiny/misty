package me.lotiny.misty.bukkit.game.registry;

import com.cryptomorin.xseries.XPotion;
import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.container.Autowired;
import io.fairyproject.mc.scheduler.MCSchedulers;
import io.fairyproject.metadata.MetadataKey;
import io.fairyproject.util.CC;
import io.fairyproject.util.ConditionUtils;
import lombok.Getter;
import lombok.Setter;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.registry.CombatLogger;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.bukkit.utils.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.UUID;

public class CombatLoggerImpl implements CombatLogger {

    public static final MetadataKey<UUID> COMBAT_LOGGER_KEY = MetadataKey.create("combat-logger", UUID.class);
    @Autowired
    private static GameManager gameManager;
    @Autowired
    private static ScenarioManager scenarioManager;
    @Getter
    private final String playerName;
    @Getter
    private final UUID playerUniqueId;

    @Setter
    @Getter
    private String nameFormat;
    @Getter
    private Villager spawnedEntity;

    public CombatLoggerImpl(Player player) {
        this.nameFormat = CC.YELLOW + "%s";
        this.playerName = player.getName();
        this.playerUniqueId = player.getUniqueId();
        gameManager.getCombatLoggerCooldown().addCooldown(this);
    }

    @Override
    public void spawn(Location location) {
        World world = location.getWorld();
        ConditionUtils.notNull(world, "Cannot spawn Combat Logger in null world");

        Villager entity = world.spawn(location, Villager.class);

        Metadata.provideForEntity(entity).put(COMBAT_LOGGER_KEY, this.playerUniqueId);
        gameManager.getRegistry().getCombatLoggers().put(entity.getUniqueId(), this);

        //noinspection deprecation
        entity.setCustomName(CC.translate(this.nameFormat));
        entity.setCustomNameVisible(true);
        entity.setRemoveWhenFarAway(false);
        entity.setCanPickupItems(false);
        PotionEffect effect = XPotion.SLOWNESS.buildPotionEffect(Integer.MAX_VALUE, 254);
        if (effect != null) {
            entity.addPotionEffect(effect);
        }

        this.spawnedEntity = entity;
    }

    @Override
    public void remove() {
        if (!spawnedEntity.isDead() && spawnedEntity.isValid()) {
            Location location = spawnedEntity.getLocation().clone();

            gameManager.getRegistry().getCombatLoggers().remove(spawnedEntity.getUniqueId());

            MCSchedulers.getGlobalScheduler().schedule(() -> {
                spawnedEntity.remove();

                dropInventory();
                UHCUtils.dropItem(location, GoldenHead.build());
                scenarioManager.dropScenarioItems(location);
            });

            gameManager.disqualifyPlayer(playerUniqueId);
        }
    }

    @Override
    public void dropInventory() {
        Snapshot snapshot = Metadata.provideForPlayer(playerUniqueId).getOrThrow(KeyEx.SNAPSHOT_KEY);
        ItemStack[] inventory = snapshot.getItems();
        ItemStack[] armor = snapshot.getArmors();

        for (ItemStack item : inventory) {
            if (ItemStackUtils.isNull(item)) continue;
            UHCUtils.dropItem(spawnedEntity.getLocation(), item);
        }

        for (ItemStack item : armor) {
            if (ItemStackUtils.isNull(item)) continue;
            UHCUtils.dropItem(spawnedEntity.getLocation(), item);
        }
    }
}
