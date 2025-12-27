package me.lotiny.misty.api.game.registry;

import org.bukkit.Location;
import org.bukkit.entity.Villager;

import java.util.UUID;

public interface CombatLogger {

    String getPlayerName();

    UUID getPlayerUniqueId();

    String getNameFormat();

    void setNameFormat(String name);

    Villager getSpawnedEntity();

    void spawn(Location location);

    void remove();

    void dropInventory();
}
