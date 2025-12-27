package me.lotiny.misty.bukkit.utils.scenario;

import com.cryptomorin.xseries.XSound;
import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.bukkit.util.BukkitPos;
import io.fairyproject.container.Autowired;
import io.fairyproject.mc.hologram.Hologram;
import io.fairyproject.mc.hologram.line.HologramLine;
import io.fairyproject.mc.scheduler.MCSchedulers;
import io.fairyproject.mc.util.Position;
import io.fairyproject.scheduler.ScheduledTask;
import io.fairyproject.scheduler.response.TaskResponse;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.scenario.impl.SafelootScenario;
import me.lotiny.misty.bukkit.scenario.impl.TimebombScenario;
import me.lotiny.misty.bukkit.utils.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class Coffin {

    @Autowired
    private static ScenarioManager scenarioManager;

    private final Player victim;
    private final Player killer;

    private final ItemStack[] items;
    private final ItemStack[] armors;

    private int time = 30;

    public void spawn(Location location) {
        Block chest = location.getBlock();
        chest.setType(Material.CHEST);
        chest.getRelative(BlockFace.UP).setType(Material.AIR);
        Metadata.provideForBlock(chest).put(TimebombScenario.CHEST_KEY, chest.getLocation());
        if (scenarioManager.isEnabled("Safeloot") && killer != null) {
            Metadata.provideForBlock(chest).put(SafelootScenario.CHEST_KEY, UHCUtils.getTeam(killer));
        }

        Block anotherChest;
        int totalSlot = armors.length + items.length + scenarioManager.getDroppedItems().size() + 1;
        if (totalSlot > 27) {
            anotherChest = chest.getRelative(BlockFace.EAST);
            anotherChest.setType(Material.CHEST);
            anotherChest.getRelative(BlockFace.UP).setType(Material.AIR);
            if (VersionUtils.isHigher(21, 0)) {
                new DoubleChestConnector(chest, anotherChest)
                        .connect();
            }

            Metadata.provideForBlock(anotherChest).put(TimebombScenario.CHEST_KEY, anotherChest.getLocation());
            if (scenarioManager.isEnabled("Safeloot") && killer != null) {
                Metadata.provideForBlock(anotherChest).put(SafelootScenario.CHEST_KEY, UHCUtils.getTeam(killer));
            }
        } else {
            anotherChest = null;
        }

        Chest chestState = (Chest) chest.getState();
        for (ItemStack item : armors) {
            if (item != null && item.getType() != Material.AIR) {
                chestState.getInventory().addItem(item);
            }
        }

        for (ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                chestState.getInventory().addItem(item);
            }
        }

        for (ItemStack item : scenarioManager.getDroppedItems()) {
            if (item != null && item.getType() != Material.AIR) {
                chestState.getInventory().addItem(item);
            }
        }

        chestState.getInventory().addItem(GoldenHead.build());

        Location hologramLocation = chest.getLocation().clone().add(anotherChest == null ? 0.5 : 1, 1, 0.5);
        hologramLocation.setYaw(-180);
        hologramLocation.setPitch(0);


        Position hologramPos = BukkitPos.toMCPos(hologramLocation);
        Hologram hologram = Hologram.create(hologramPos)
                .line(HologramLine.create(Component.text("30...", NamedTextColor.GREEN)))
                .spawn();
        ScheduledTask<String> schedule = MCSchedulers.getGlobalScheduler().scheduleAtFixedRate(() -> {
            --time;

            if (time == 0) {
                return TaskResponse.success(victim.getName());
            }

            TextColor color;
            if (time <= 5) {
                XSound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON.play(hologramLocation);
                color = NamedTextColor.DARK_RED;
            } else if (time <= 10) {
                color = NamedTextColor.RED;
            } else if (time <= 15) {
                color = NamedTextColor.GOLD;
            } else if (time <= 20) {
                color = NamedTextColor.YELLOW;
            } else {
                color = NamedTextColor.GREEN;
            }

            hologram.line(0, HologramLine.create(Component.text(time + "...", color)));

            return TaskResponse.continueTask();
        }, 0L, 20L);

        schedule.getFuture().whenComplete((s, throwable) -> {
            delete(chest);
            if (anotherChest != null) {
                delete(anotherChest);
            }

            hologram.remove();
            PlayerUtils.playSound(chest.getLocation(), XSound.ENTITY_GENERIC_EXPLODE);
            Utilities.broadcast(Message.TIMEBOMB_EXPLODE.toString()
                    .replace("<player>", s));
        });
    }

    private void delete(Block block) {
        if (block.getState() instanceof Chest blockState) {
            blockState.getInventory().clear();
        }

        Metadata.provideForBlock(block).clear();
        block.setType(Material.AIR);
    }
}
