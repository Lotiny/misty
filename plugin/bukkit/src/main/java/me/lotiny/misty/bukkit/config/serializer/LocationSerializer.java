package me.lotiny.misty.bukkit.config.serializer;

import de.exlll.configlib.Serializer;
import io.fairyproject.bukkit.util.BukkitPos;
import io.fairyproject.mc.util.Position;
import org.bukkit.Location;

public class LocationSerializer implements Serializer<Location, String> {

    @Override
    public String serialize(Location location) {
        return BukkitPos.toMCPos(location).toString();
    }

    @Override
    public Location deserialize(String s) {
        return BukkitPos.toBukkitLocation(Position.fromString(s));
    }
}
