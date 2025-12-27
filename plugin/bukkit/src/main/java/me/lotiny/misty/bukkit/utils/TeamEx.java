package me.lotiny.misty.bukkit.utils;

import io.fairyproject.data.MetaKey;
import io.fairyproject.metadata.MetadataKey;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

@UtilityClass
public class TeamEx {

    public MetaKey<Boolean> TEAM_FILL = MetaKey.create("misty:team-fill", Boolean.class);
    public MetaKey<Integer> TEAM_KILLS = MetaKey.create("misty:team-kills", Integer.class);
    public MetaKey<Location> SCATTER_LOCATION = MetaKey.create("misty:scatter-location", Location.class);
    public MetaKey<Inventory> TEAM_INVENTORY = MetaKey.create("misty:team-inventory", Inventory.class);
    public MetadataKey<Boolean> TEAM_CHAT = MetadataKey.create("misty:team-chat", Boolean.class);
}
