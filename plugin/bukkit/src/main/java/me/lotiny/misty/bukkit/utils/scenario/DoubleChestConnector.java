package me.lotiny.misty.bukkit.utils.scenario;

import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Chest;

@RequiredArgsConstructor
public class DoubleChestConnector {

    private final Block chest;
    private final Block anotherChest;

    public void connect() {
        Chest chestData = (Chest) chest.getBlockData();
        Chest anotherChestData = (Chest) anotherChest.getBlockData();

        chestData.setType(Chest.Type.LEFT);
        anotherChestData.setType(Chest.Type.RIGHT);

        chest.setBlockData(chestData, true);
        anotherChest.setBlockData(anotherChestData, true);
    }
}
