package me.lotiny.misty.bukkit.hook.impl.chunk;

public interface ChunkLoader {

    void fillWorld(String world, int size);

    void setSize(String world, int size);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean isCompleted();

    String getWorld();

    float getProgress();

    long getChunks();
}
