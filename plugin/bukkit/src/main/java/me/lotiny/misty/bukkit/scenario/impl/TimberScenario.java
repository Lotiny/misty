package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.mc.scheduler.MCSchedulers;
import io.fairyproject.scheduler.ScheduledTask;
import me.lotiny.misty.api.scenario.Scenario;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Predicate;

public class TimberScenario extends Scenario {

    @Override
    public String getName() {
        return "Timber";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.WOODEN_AXE)
                .name("&b" + getName())
                .lore(
                        "&7When you break a tree, the entire logs of that tree will come off."
                )
                .build();
    }

    @EventHandler
    public void handleBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (event.isCancelled() || player.getGameMode() == GameMode.CREATIVE) return;

        Block block = event.getBlock();
        if (XTag.LOGS.isTagged(XMaterial.matchXMaterial(block.getType()))) {
            List<Vector> treeLogs = new ArrayList<>(floodFind(block,
                    b -> XTag.LOGS.isTagged(XMaterial.matchXMaterial(b.getType()))));

            World world = block.getWorld();
            treeLogs.sort(Comparator.comparingDouble(v -> v.distance(block.getLocation().toVector())));

            final int[] index = {0};
            final ScheduledTask<?>[] taskHolder = new ScheduledTask<?>[1];
            taskHolder[0] = MCSchedulers.getGlobalScheduler().scheduleAtFixedRate(() -> {
                if (index[0] >= treeLogs.size()) {
                    taskHolder[0].cancel();
                    return;
                }

                Vector v = treeLogs.get(index[0]);
                Block wood = world.getBlockAt(v.getBlockX(), v.getBlockY(), v.getBlockZ());

                if (XTag.LOGS.isTagged(XMaterial.matchXMaterial(wood.getType()))) {
                    wood.breakNaturally();
                    wood.getWorld().playEffect(wood.getLocation(), Effect.STEP_SOUND, wood.getType());
                }

                index[0]++;
            }, 0L, 4L);
        }
    }

    private Set<Vector> floodFind(Block origin, Predicate<Block> isInside) {
        Set<Vector> found = new HashSet<>();
        Queue<Block> queue = new ArrayDeque<>();
        queue.add(origin);
        found.add(new Vector(origin.getX(), origin.getY(), origin.getZ()));

        while (!queue.isEmpty() && found.size() < 48) {
            final Block current = queue.remove();

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        final Block neighbor = current.getRelative(dx, dy, dz);
                        final Vector neighborPos = new Vector(neighbor.getX(), neighbor.getY(), neighbor.getZ());
                        if (isInside.test(neighbor) && found.add(neighborPos)) {
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }
        return found;
    }
}
