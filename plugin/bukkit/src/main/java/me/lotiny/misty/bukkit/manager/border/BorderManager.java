package me.lotiny.misty.bukkit.manager.border;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.mc.scheduler.MCSchedulers;
import io.fairyproject.metadata.MetadataKey;
import io.fairyproject.scheduler.ScheduledTask;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.event.BorderShrunkEvent;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.registry.CombatLogger;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.api.team.TeamManager;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.impl.MainConfig;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.hook.PluginHookManager;
import me.lotiny.misty.bukkit.manager.WorldManager;
import me.lotiny.misty.bukkit.utils.MaterialUtils;
import me.lotiny.misty.bukkit.utils.ReflectionUtils;
import me.lotiny.misty.bukkit.utils.Utilities;
import me.lotiny.misty.bukkit.utils.VersionUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@InjectableComponent
@RequiredArgsConstructor
public class BorderManager {

    private final int BATCH_SIZE = 500;
    private final int PROCESSING_DELAY_TICKS = 5;
    private final MetadataKey<Integer> BORDER_KEY = MetadataKey.createIntegerKey("misty:border-block");

    private final GameManager gameManager;
    private final TeamManager teamManager;
    private final WorldManager worldManager;
    private final ScenarioManager scenarioManager;
    private final PluginHookManager pluginHookManager;

    private int randomTeleport;
    private int borderHeight;
    private int borderInterval;
    private int[] allowedBorderSizes;
    private XMaterial visualBorderBlock;
    private XMaterial borderBlock;

    @PostInitialize
    public void onPostInit() {
        MainConfig.Border border = Config.getMainConfig().getBorder();
        this.randomTeleport = border.getRandomTeleport();
        this.borderHeight = border.getHeight();
        this.borderInterval = border.getInterval();
        this.allowedBorderSizes = border.getAllowedSize().stream().mapToInt(i -> i).toArray();
        this.visualBorderBlock = border.getVisualBorder();
        this.borderBlock = border.getBlock();
    }

    public void handleStartSeconds(int size) {
        GameRegistry registry = gameManager.getRegistry();

        Utilities.broadcast(Message.BORDER_SHRUNK.toString().replace("<size>", String.valueOf(size)));

        World uhcWorld = Bukkit.getWorld(registry.getUhcWorld());
        if (uhcWorld != null) {
            shrinkBorder(uhcWorld, size);
        }

        if (gameManager.getGame().getSetting().isNether()) {
            World netherWorld = Bukkit.getWorld(registry.getNetherWorld());
            if (netherWorld != null) {
                shrinkBorder(netherWorld, size / 2);
            }
        }
    }

    public int getNextBorder() {
        int currentBorder = gameManager.getGame().getSetting().getBorderSize();

        for (int i = 0; i < allowedBorderSizes.length; i++) {
            if (allowedBorderSizes[i] == currentBorder) {
                return i > 0 ? allowedBorderSizes[i - 1] : currentBorder;
            }
        }

        return allowedBorderSizes[0];
    }

    private void shrinkBorder(World world, int size) {
        String worldName = world.getName();
        int lastBorderSize = allowedBorderSizes[0];
        int netherBorderSize = 500;

        MCSchedulers.getGlobalScheduler().schedule(() -> {
            if (world.getEnvironment() != World.Environment.NORMAL) return;

            shrinkBorder(worldName, size, getBorderHeight());
            gameManager.getGame().getSetting().setBorderSize(size);

            if (size == netherBorderSize) {
                World nether = Bukkit.getWorld(gameManager.getRegistry().getNetherWorld());
                if (nether != null) {
                    for (Player player : nether.getPlayers()) {
                        gameManager.teleportToRandomLocation(player, netherBorderSize);
                    }
                    worldManager.unloadWorld(nether);
                }
            } else if (size == 100 && !scenarioManager.isEnabled("Permanight")) {
                world.setTime(1000);
                ReflectionUtils.get().setGameRule(world, "doDaylightCycle", false);
                Utilities.broadcast("&bPermanently Day &ehas been enabled!");
            } else if (size == lastBorderSize && gameManager.getGame().getSetting().isLastBorderFlat()) {
                createFlatGround(world, lastBorderSize);
                gameManager.getRegistry().getAlivePlayers().stream()
                        .map(Bukkit::getPlayer)
                        .filter(Objects::nonNull)
                        .filter(player -> player.getLocation().getBlockY() >= 60 && isInBorder(player, size))
                        .forEach(player -> {
                            Location location = player.getLocation();
                            int highestY = player.getWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ());
                            Location target = location.clone();
                            target.setY(highestY);
                            player.teleport(target);
                        });

                gameManager.getRegistry().setCanShrink(false);
            }

            MCSchedulers.getGlobalScheduler().schedule(() -> pluginHookManager.getChunkLoader().setSize(worldName, size), 20L);

            Bukkit.getPluginManager().callEvent(new BorderShrunkEvent(size, world));
            checkBorder(size, world, size <= randomTeleport);
        });
    }

    private void createFlatGround(World world, int size) {
        int minY = 54;
        int maxY = 150;
        Material bedrock = MaterialUtils.getMaterial(XMaterial.BEDROCK);
        Material grass = MaterialUtils.getMaterial(XMaterial.GRASS_BLOCK);
        Material air = MaterialUtils.getMaterial(XMaterial.AIR);

        int chunkMin = -size >> 4;
        int chunkMax = size >> 4;

        for (int chunkX = chunkMin; chunkX <= chunkMax; chunkX++) {
            for (int chunkZ = chunkMin; chunkZ <= chunkMax; chunkZ++) {
                Chunk chunk = world.getChunkAt(chunkX, chunkZ);

                for (int cx = 0; cx < 16; cx++) {
                    int worldX = (chunkX << 4) + cx;
                    if (Math.abs(worldX) > size) continue;

                    for (int cz = 0; cz < 16; cz++) {
                        int worldZ = (chunkZ << 4) + cz;
                        if (Math.abs(worldZ) > size) continue;

                        for (int y = minY; y <= maxY; y++) {
                            Block block = chunk.getBlock(cx, y, cz);
                            if (y == minY) {
                                block.setType(bedrock);
                            } else if (y == minY + 1) {
                                block.setType(grass);
                            } else {
                                block.setType(air);
                            }
                        }
                    }
                }
            }
        }
    }

    private void checkBorder(int size, World world, boolean randomTeleportEnabled) {
        GameRegistry registry = gameManager.getRegistry();
        int minCoord = -size + 5;
        int maxCoord = size - 5;

        if (randomTeleportEnabled) {
            for (Team team : teamManager.getTeams().values()) {
                List<LivingEntity> outsideEntities = team.getMembers(true).stream()
                        .map(uuid -> getLivingEntity(uuid, registry))
                        .filter(Objects::nonNull)
                        .filter(entity -> !isInBorder(entity, size) || (entity.getLocation().getBlockY() < 55 && size < 100))
                        .toList();

                if (outsideEntities.isEmpty()) continue;

                int x = ThreadLocalRandom.current().nextInt(minCoord, maxCoord);
                int z = ThreadLocalRandom.current().nextInt(minCoord, maxCoord);
                int y = world.getHighestBlockYAt(x, z);
                Location location = new Location(world, x, y + 2, z);

                outsideEntities.forEach(entity -> teleportAndResetFallDistance(entity, location.clone()));
            }
        } else {
            registry.getAlivePlayers().stream()
                    .map(uuid -> getLivingEntity(uuid, registry))
                    .filter(Objects::nonNull)
                    .filter(entity -> !isInBorder(entity, size))
                    .forEach(entity -> {
                        Location loc = findLocation(entity, size).add(0, 2, 0);
                        teleportAndResetFallDistance(entity, loc);
                    });
        }
    }

    private LivingEntity getLivingEntity(UUID uuid, GameRegistry registry) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            return player;
        }

        CombatLogger logger = registry.getCombatLoggers().get(uuid);
        return logger != null ? logger.getSpawnedEntity() : null;
    }

    private void teleportAndResetFallDistance(LivingEntity entity, Location location) {
        if (entity != null && location != null) {
            entity.teleport(location);
            entity.setFallDistance(0);
        }
    }

    private Location findLocation(LivingEntity entity, int size) {
        if (entity == null) {
            return null;
        }

        Location loc = entity.getLocation();
        World world = loc.getWorld();
        if (world == null) {
            return loc;
        }

        int x = Math.max(-size + 3, Math.min(loc.getBlockX(), size - 3));
        int z = Math.max(-size + 3, Math.min(loc.getBlockZ(), size - 3));
        int y = world.getHighestBlockYAt(x, z);

        return new Location(world, x, y, z);
    }

    public void shrinkBorder(String worldName, int radius, int blocksHigh) {
        for (int i = 0; i < blocksHigh; i++) {
            MCSchedulers.getGlobalScheduler().schedule(() -> addBorder(worldName, radius), i);
        }
    }

    public void figureOutBlockToMakeBedrock(String worldName, int x, int z) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) return;

        Block block = world.getHighestBlockAt(x, z);
        if (VersionUtils.isHigher(21, 4)) {
            block = block.getRelative(BlockFace.UP);
        }

        Block below = block.getRelative(BlockFace.DOWN);
        XMaterial material = XMaterial.matchXMaterial(below.getType());
        while (isBlockedWallBlocks(material) && below.getY() > 5) {
            below = below.getRelative(BlockFace.DOWN);
            material = XMaterial.matchXMaterial(below.getType());
        }

        Block toReplace = below.getRelative(BlockFace.UP);
        while (Metadata.provideForBlock(toReplace).has(BORDER_KEY)) {
            toReplace = toReplace.getRelative(BlockFace.UP);
        }

        setBorderBlock(toReplace.getLocation());
    }

    private boolean isBlockedWallBlocks(XMaterial xMaterial) {
        return XTag.LEAVES.isTagged(xMaterial) ||
                XTag.LOGS.isTagged(xMaterial) ||
                XTag.FLOWERS.isTagged(xMaterial) ||
                xMaterial == XMaterial.AIR ||
                xMaterial == XMaterial.BROWN_MUSHROOM_BLOCK ||
                xMaterial == XMaterial.RED_MUSHROOM_BLOCK ||
                xMaterial == XMaterial.CACTUS ||
                xMaterial == XMaterial.DEAD_BUSH ||
                xMaterial == XMaterial.SUGAR_CANE ||
                xMaterial == XMaterial.ICE ||
                xMaterial == XMaterial.SNOW;
    }

    private void setBorderBlock(Location location) {
        Block block = location.getBlock();
        block.setType(MaterialUtils.getMaterial(borderBlock));
        block.getState().update(false);
        Metadata.provideForBlock(block).put(BORDER_KEY, 0);
    }

    private boolean isInBorder(LivingEntity entity, int size) {
        Location loc = entity.getLocation();
        double x = loc.getX();
        double z = loc.getZ();
        return Math.abs(x) <= size && Math.abs(z) <= size;
    }

    public void addBorder(String world, int radius) {
        AtomicReference<ScheduledTask<?>> taskRef = new AtomicReference<>();
        AtomicInteger phase = new AtomicInteger(0);
        AtomicInteger counter = new AtomicInteger(-radius);

        ScheduledTask<?> task = MCSchedulers.getGlobalScheduler().scheduleAtFixedRate(() -> {
            int currentPhase = phase.get();
            int start = counter.get();
            int end = Math.min(start + BATCH_SIZE, radius);

            switch (currentPhase) {
                case 0 -> {
                    for (int z = start; z <= end; z++) {
                        figureOutBlockToMakeBedrock(world, -radius, z);
                    }
                    nextPhase(end, 1, counter, phase, radius);
                }
                case 1 -> {
                    for (int z = start; z <= end; z++) {
                        figureOutBlockToMakeBedrock(world, radius, z);
                    }
                    nextPhase(end, 2, counter, phase, radius);
                }
                case 2 -> {
                    for (int x = start; x <= end; x++) {
                        if (x != -radius && x != radius) {
                            figureOutBlockToMakeBedrock(world, x, -radius);
                        }
                    }
                    nextPhase(end, 3, counter, phase, radius - 1);
                }
                case 3 -> {
                    for (int x = start; x <= end; x++) {
                        if (x != -radius && x != radius) {
                            figureOutBlockToMakeBedrock(world, x, radius);
                        }
                    }

                    if (end >= radius - 1) {
                        taskRef.get().cancel();
                    } else {
                        counter.set(end + 1);
                    }
                }
            }
        }, 0L, PROCESSING_DELAY_TICKS);

        taskRef.set(task);
    }

    private void nextPhase(int end, int nextPhase, AtomicInteger counter, AtomicInteger phase, int limit) {
        if (end >= limit) {
            phase.set(nextPhase);
            counter.set(-limit);
        } else {
            counter.set(end + 1);
        }
    }
}