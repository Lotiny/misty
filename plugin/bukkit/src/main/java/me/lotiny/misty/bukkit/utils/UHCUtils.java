package me.lotiny.misty.bukkit.utils;

import com.cryptomorin.xseries.XEntityType;
import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.container.Autowired;
import io.fairyproject.log.Log;
import io.fairyproject.util.CC;
import lombok.experimental.UtilityClass;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.profile.stats.Stats;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.bukkit.kit.Kit;
import me.lotiny.misty.bukkit.kit.KitManager;
import me.lotiny.misty.bukkit.scenario.impl.NoCleanScenario;
import me.lotiny.misty.bukkit.utils.cooldown.CombatCooldown;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@UtilityClass
public class UHCUtils {

    @Autowired
    private static GameManager gameManager;
    @Autowired
    private static ScenarioManager scenarioManager;
    @Autowired
    private static KitManager kitManager;

    public boolean isAlive(UUID uuid) {
        return gameManager.getRegistry().getAlivePlayers().contains(uuid);
    }

    public String getGameType() {
        String redVsBlue = "Red vs Blue";
        if (scenarioManager.isEnabled("Love At First Sight")) {
            return "LAFS";
        }

        if (scenarioManager.isEnabled(redVsBlue)) {
            return redVsBlue;
        }

        if (gameManager.getGame().getSetting().getTeamSize() > 1) {
            return "To" + gameManager.getGame().getSetting().getTeamSize();
        }

        return "FFA";
    }

    public void sendAlert(String message) {
        Bukkit.getConsoleSender().sendMessage(CC.translate(message));
        for (UUID uuid : gameManager.getRegistry().getSpectators()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.hasPermission("misty.alerts")) {
                player.sendMessage(CC.translate(message));
            }
        }
    }

    public void giveStarterItem(Player player) {
        boolean isRandomKit = kitManager.isRandom();
        Kit kit = !isRandomKit ? kitManager.getDefaultKit() : kitManager.getRandomKit();
        if (kit == null) {
            Log.error("Kit not found");
            return;
        }

        player.getInventory().setArmorContents(kit.getArmors());
        player.getInventory().setContents(kit.getItems());
    }

    @SuppressWarnings("DataFlowIssue")
    public Location getCenter() {
        GameRegistry registry = gameManager.getRegistry();
        World world = Bukkit.getWorld(registry.getUhcWorld());
        int highestY = world.getHighestBlockYAt(0, 0);
        return new Location(world, 0, highestY + 10, 0);
    }

    public void dropItem(Location location, ItemStack item) {
        Location dropLocation = location.add(0.5, 0.5, 0.5);
        dropLocation.getWorld().dropItem(dropLocation, item);
    }

    @SuppressWarnings("DataFlowIssue")
    public void spawnXpOrb(Location location, int quantity) {
        ExperienceOrb orb = (ExperienceOrb) location.getWorld().spawnEntity(location, XEntityType.EXPERIENCE_ORB.get());
        orb.setExperience(quantity);
    }

    public boolean hasNoClean(Player player) {
        return Metadata.provideForPlayer(player).getOrNull(NoCleanScenario.NO_CLEAN_KEY) != null;
    }

    public boolean isInCombat(Team team) {
        return team.getStorage().getOrNull(KeyEx.COMBAT_COOLDOWN_KEY) != null;
    }

    public boolean isCombatWith(@NotNull Team team, Team target) {
        CombatCooldown cooldown = team.getStorage().getOrNull(KeyEx.COMBAT_COOLDOWN_KEY);
        return cooldown != null && cooldown.getTeam().isSame(target);
    }

    public Team getTeam(Player player) {
        return Metadata.provideForPlayer(player).getOrNull(KeyEx.TEAM_KEY);
    }

    public void setTeam(Player player, Team team) {
        if (team == null) {
            Metadata.provideForPlayer(player).remove(KeyEx.TEAM_KEY);
        } else {
            Metadata.provideForPlayer(player).put(KeyEx.TEAM_KEY, team);
        }
    }

    public boolean isSameTeam(Player player, Player target) {
        return getTeam(player).isSame(getTeam(target));
    }

    public int getGameKills(Player player) {
        return Metadata.provideForPlayer(player).getOrDefault(KeyEx.GAME_KILLS_KEY, new Stats()).getAmount();
    }

    public void increaseGameKills(Player player) {
        Stats stats = Metadata.provideForPlayer(player).getOrDefault(KeyEx.GAME_KILLS_KEY, new Stats());
        stats.increase();
        Metadata.provideForPlayer(player).put(KeyEx.GAME_KILLS_KEY, stats);
    }
}
