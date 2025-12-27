package me.lotiny.misty.bukkit.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.lotiny.misty.api.team.Team;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class Snapshot {

    private final UUID uuid;

    private int level;
    private float exp;
    private double health;
    private double maxHealth;
    private ItemStack[] items;
    private ItemStack[] armors;

    private List<PotionEffect> effects = new ArrayList<>();

    private Location location;
    private Team team;
    private int elo = 0;
}
