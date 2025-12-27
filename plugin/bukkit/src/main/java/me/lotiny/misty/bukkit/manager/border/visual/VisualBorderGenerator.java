package me.lotiny.misty.bukkit.manager.border.visual;

import io.fairyproject.bukkit.visual.VisualBlockGenerator;
import io.fairyproject.bukkit.visual.VisualPosition;
import io.fairyproject.bukkit.visual.type.MaterialVisualType;
import io.fairyproject.bukkit.visual.type.VisualType;
import io.fairyproject.container.Autowired;
import me.lotiny.misty.bukkit.manager.border.BorderManager;
import me.lotiny.misty.bukkit.manager.border.visual.impl.NetherBorderProvider;
import me.lotiny.misty.bukkit.manager.border.visual.impl.OverworldBorderProvider;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Set;

public class VisualBorderGenerator implements VisualBlockGenerator {

    @Autowired
    private static BorderManager borderManager;

    private final VisualType borderVisualType;
    private final BorderProvider overworldBorderProvider, netherBorderProvider;

    public VisualBorderGenerator() {
        this.borderVisualType = MaterialVisualType.builder().material(borderManager.getVisualBorderBlock()).build();
        this.overworldBorderProvider = new OverworldBorderProvider();
        this.netherBorderProvider = new NetherBorderProvider();
    }

    @Override
    public void generate(Player player, Location location, Set<VisualPosition> positions) {
        World world = location.getWorld();
        if (world == null) return;

        BorderProvider borderProvider = world.getEnvironment() == World.Environment.NETHER
                ? netherBorderProvider
                : overworldBorderProvider;

        int minX = borderProvider.getMinX();
        int maxX = borderProvider.getMaxX();
        int minZ = borderProvider.getMinZ();
        int maxZ = borderProvider.getMaxZ();

        int playerX = location.getBlockX();
        int playerZ = location.getBlockZ();

        int closestX = closestNumber(playerX, minX, maxX);
        int closestZ = closestNumber(playerZ, minZ, maxZ);

        int renderDistance = 10;
        boolean nearXBorder = Math.abs(playerX - closestX) < renderDistance;
        boolean nearZBorder = Math.abs(playerZ - closestZ) < renderDistance;

        if (!nearXBorder && !nearZBorder) return;

        int baseY = location.getBlockY();

        if (nearXBorder) {
            generateBorderLine(positions, world, closestX, baseY, playerZ, minZ, maxZ, true);
        }

        if (nearZBorder) {
            generateBorderLine(positions, world, playerX, baseY, closestZ, minX, maxX, false);
        }
    }

    private void generateBorderLine(Collection<VisualPosition> positions, World world, int fixedX, int baseY, int fixedZ, int min, int max, boolean isXAxis) {
        int verticalRange = 5;

        for (int yOffset = -verticalRange; yOffset <= verticalRange; yOffset++) {
            for (int offset = -verticalRange; offset <= verticalRange; offset++) {
                int variableCoord = isXAxis ? fixedZ + offset : fixedX + offset;

                if (isInBetween(min, max, variableCoord)) {
                    int x = isXAxis ? fixedX : variableCoord;
                    int z = isXAxis ? variableCoord : fixedZ;
                    int y = baseY + yOffset;

                    positions.add(new VisualPosition(x, y, z,
                            world.getName(), borderVisualType));
                }
            }
        }
    }

    private boolean isInBetween(int bound1, int bound2, int value) {
        int min = Math.min(bound1, bound2);
        int max = Math.max(bound1, bound2);
        return value >= min && value <= max;
    }

    private int closestNumber(int target, int... numbers) {
        if (numbers.length == 0) {
            throw new IllegalArgumentException("Numbers array must not be empty");
        }

        int closest = numbers[0];
        int minDistance = Math.abs(target - closest);

        for (int number : numbers) {
            int distance = Math.abs(target - number);
            if (distance < minDistance) {
                minDistance = distance;
                closest = number;
            }
        }

        return closest;
    }
}
