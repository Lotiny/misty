package me.lotiny.misty.bukkit.game.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.XTag;
import io.fairyproject.Fairy;
import io.fairyproject.bukkit.events.player.EntityDamageByPlayerEvent;
import io.fairyproject.bukkit.events.player.PlayerDamageByPlayerEvent;
import io.fairyproject.bukkit.events.player.PlayerDamageEvent;
import io.fairyproject.container.Autowired;
import io.fairyproject.util.CC;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameSetting;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.api.profile.stats.StatType;
import me.lotiny.misty.bukkit.Permission;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.impl.MainConfig;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.manager.WorldManager;
import me.lotiny.misty.bukkit.storage.StorageRegistry;
import me.lotiny.misty.bukkit.task.GameTask;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.ReflectionUtils;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class GameListener implements Listener {

    @Autowired
    private static GameManager gameManager;
    @Autowired
    private static StorageRegistry storageRegistry;
    @Autowired
    private static WorldManager worldManager;

    private final MainConfig.Healing.HealingItem goldenHead;
    private final MainConfig.Healing.HealingItem goldenApple;
    private final MainConfig.Healing.HealingItem playerHead;

    public GameListener() {
        MainConfig.Healing healing = Config.getMainConfig().getHealing();
        this.goldenHead = healing.getGoldenHead();
        this.goldenApple = healing.getGoldenApple();
        this.playerHead = healing.getPlayerHead();
    }

    @EventHandler
    public void handlePrepareItemCraftEvent(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe == null) return;

        if (recipe.getResult().isSimilar(XMaterial.ENCHANTED_GOLDEN_APPLE.parseItem())) {
            if (gameManager.getGame().getSetting().isGodApple()) return;

            event.getInventory().setResult(XMaterial.AIR.parseItem());
        }
    }

    @EventHandler
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!UHCUtils.isAlive(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        ItemStack item = PlayerUtils.getItemInHand(player);
        if (item == null || item.getType() != XMaterial.POTION.get()) return;

        XPotion xPotion = ReflectionUtils.get().getPotionEffect(item);
        if (xPotion == null) return;

        int level = ReflectionUtils.get().getPotionEffectLevel(item);
        GameSetting setting = gameManager.getGame().getSetting();

        boolean shouldRemove = switch (xPotion) {
            case SPEED -> (level == 1 && !setting.isSpeed1()) || (level == 2 && !setting.isSpeed2());
            case STRENGTH -> (level == 1 && !setting.isStrength1()) || (level == 2 && !setting.isStrength2());
            case POISON -> !setting.isPoison();
            case INVISIBILITY -> !setting.isInvisible();
            default -> false;
        };

        if (shouldRemove) {
            event.setCancelled(true);
            PlayerUtils.setItemInHand(player, null);
            player.sendMessage(CC.translate("&cThis potion is not allowed!"));
        }
    }

    @EventHandler
    public void handleAppleDrop(LeavesDecayEvent event) {
        if (!event.isCancelled()) {
            GameSetting setting = gameManager.getGame().getSetting();
            int rate = setting.getAppleRate();
            if (Fairy.random().nextInt(100) < rate) {
                Block block = event.getBlock();
                UHCUtils.dropItem(block.getLocation(), XMaterial.APPLE.parseItem());
            }
        }
    }

    @EventHandler
    public void handleShears(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = PlayerUtils.getItemInHand(player);
        if (item == null || item.getType() != Material.SHEARS) return;

        Block block = event.getBlock();
        Material blockType = block.getType();
        if (XTag.LEAVES.isTagged(XMaterial.matchXMaterial(blockType))) {
            int rate = gameManager.getGame().getSetting().getAppleRate();
            if (Fairy.random().nextInt(100) < rate) {
                UHCUtils.dropItem(block.getLocation(), XMaterial.APPLE.parseItem());
            }
        }
    }

    @EventHandler
    public void handleBlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        GameRegistry registry = gameManager.getRegistry();
        if (player.getGameMode() == GameMode.CREATIVE) return;

        if (!UHCUtils.isAlive(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        Block placedBlock = event.getBlockPlaced();

        if (placedBlock.getType() != Material.FIRE) return;

        if (!registry.isPvpEnabled()) {
            for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
                if (entity instanceof Player target) {
                    if (!UHCUtils.isAlive(target.getUniqueId())) return;

                    event.setCancelled(true);
                    player.sendMessage(CC.translate("&cYou are not allowed to place or fire in grace period to prevent iPvP."));
                    UHCUtils.sendAlert("&c" + player.getName() + " try to iPvP " + target.getName() + "!");
                    return;
                }
            }
        }
    }

    @EventHandler
    public void handlePlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Material bucket = event.getBucket();
        GameRegistry registry = gameManager.getRegistry();
        if (bucket != Material.LAVA_BUCKET) return;

        if (!registry.isPvpEnabled()) {
            for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
                if (entity instanceof Player target) {
                    if (!UHCUtils.isAlive(target.getUniqueId())) return;

                    event.setCancelled(true);
                    player.sendMessage(CC.translate("&cYou are not allowed to place lava or fire in grace period to prevent iPvP."));
                    UHCUtils.sendAlert("&c" + player.getName() + " try to iPvP " + target.getName() + "!");
                }
            }
        }
    }

    @EventHandler
    public void handlePlayerDamageEntity(EntityDamageByPlayerEvent event) {
        Player player = event.getDamager();
        if (!UHCUtils.isAlive(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handlePlayerDamage(PlayerDamageEvent event) {
        GameRegistry registry = gameManager.getRegistry();
        Player player = event.getPlayer();

        if (!UHCUtils.isAlive(player.getUniqueId()) || !registry.isDamage()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handlePlayerDamagePlayer(PlayerDamageByPlayerEvent event) {
        Player player = event.getPlayer();
        Player damager = event.getDamager();

        if (!UHCUtils.isAlive(player.getUniqueId()) || !UHCUtils.isAlive(damager.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        GameRegistry registry = gameManager.getRegistry();
        if (!registry.isPvpEnabled()) {
            event.setCancelled(true);
            return;
        }

        if (UHCUtils.isSameTeam(player, damager)) {
            event.setDamage(0);
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE &&
                player.getHealth() - event.getFinalDamage() > 0.0D &&
                event.getDamage() > 0) {
            damager.sendMessage(Message.HEALTH.toString()
                    .replace("<target>", player.getName())
                    .replace("<health>", String.valueOf(Math.round(player.getHealth()))));
        }
    }

    @EventHandler
    public void handleEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player player) {
            if (!UHCUtils.isAlive(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void handleBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Profile profile = storageRegistry.getProfile(player.getUniqueId());
        if (!UHCUtils.isAlive(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (UHCUtils.isAlive(player.getUniqueId())) {
            Block block = event.getBlock();
            if (XTag.DIAMOND_ORES.isTagged(XMaterial.matchXMaterial(block.getType()))) {
                profile.getStats(StatType.DIAMOND_MINED).increase();
            }
        }
    }

    @EventHandler
    public void handlePlayerPortalEvent(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            event.setCancelled(true);
            player.sendMessage(CC.RED + "The end is disabled.");
            return;
        }

        GameRegistry registry = gameManager.getRegistry();
        GameTask gameTask = (GameTask) registry.getGameTask();
        GameSetting setting = gameManager.getGame().getSetting();
        String uhcWorld = registry.getUhcWorld();
        String uhcNether = registry.getNetherWorld();
        if (!setting.isNether()) {
            event.setCancelled(true);
            player.sendMessage(CC.RED + "Nether is currently disabled!");
            return;
        }

        if (!UHCUtils.isAlive(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(CC.RED + "You can't access the nether as a spectator.");
            return;
        }

        int netherTime = setting.getNetherTime() * 60;
        if (gameTask != null && gameTask.getSeconds() < netherTime) {
            event.setCancelled(true);
            player.sendMessage(CC.RED + "You can't access the nether before " + setting.getNetherTime() + " minute(s).");
            return;
        }

        if (setting.getBorderSize() <= 500) {
            event.setCancelled(true);
            player.sendMessage(CC.RED + "You can't access the nether after the border shrinks to 500x500.");
            return;
        }

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            ReflectionUtils.get().handleNetherPortal(event, Bukkit.getWorld(uhcWorld), Bukkit.getWorld(uhcNether), worldManager.getNetherScale());
        }
    }

    @EventHandler
    public void handlePlayerTeleportEvent(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        GameSetting setting = gameManager.getGame().getSetting();
        double border = setting.getBorderSize();
        boolean pearlDamage = setting.isPearlDamage();

        int x = Math.abs(to.getBlockX());
        int z = Math.abs(to.getBlockZ());

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            if (x > border || z > border) {
                event.setCancelled(true);
                player.getInventory().addItem(ItemStackUtils.of(XMaterial.ENDER_PEARL));
                player.sendMessage(CC.RED + "You can't pearl outside the border.");
                return;
            }

            if (!pearlDamage) {
                event.setCancelled(true);
                player.teleport(to);
            }

        } else if (!UHCUtils.isAlive(player.getUniqueId()) && !player.hasPermission(Permission.SPECTATE_BYPASS)) {
            if (x > 100 || z > 100) {
                event.setCancelled(true);
                player.teleport(UHCUtils.getCenter());
                player.sendMessage(CC.RED + "You may only spectate 100x100 blocks!");
            }
        }
    }

    @EventHandler
    public void handleFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!UHCUtils.isAlive(player.getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            if (event.getFoodLevel() < player.getFoodLevel() && Fairy.random().nextInt(10) < 8) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void handlePlayerBedEnterEvent(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        GameSetting setting = gameManager.getGame().getSetting();
        if (setting.isBedBomb()) return;

        World world = event.getBed().getLocation().getWorld();
        if (world != null && world.getEnvironment() == World.Environment.NETHER) {
            event.setCancelled(true);
            player.sendMessage(Message.BED_BOMB_DISABLED.toString());
        }
    }

    @EventHandler
    public void handleEnterVehicle(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player player) {
            if (!UHCUtils.isAlive(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }
}
