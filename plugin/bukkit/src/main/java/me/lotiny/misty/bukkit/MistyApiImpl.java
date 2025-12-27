package me.lotiny.misty.bukkit;

import io.fairyproject.bootstrap.bukkit.BukkitPlugin;
import io.fairyproject.container.Autowired;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.container.PreDestroy;
import me.lotiny.misty.api.MistyApi;
import me.lotiny.misty.api.customitem.CustomItemRegistry;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.profile.stats.StatType;
import me.lotiny.misty.api.profile.stats.Stats;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.bukkit.storage.StorageRegistry;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import java.util.UUID;

@InjectableComponent
public class MistyApiImpl implements MistyApi {

    @Autowired
    private static CustomItemRegistry customItemRegistry;
    @Autowired
    private static GameManager gameManager;
    @Autowired
    private static StorageRegistry storageRegistry;

    @PostInitialize
    public void onPostInit() {
        ServicesManager servicesManager = Bukkit.getServicesManager();
        servicesManager.register(MistyApi.class, this, BukkitPlugin.INSTANCE, ServicePriority.Normal);
    }

    @PreDestroy
    public void onPreDestroy() {
        Bukkit.getServicesManager().unregister(MistyApi.class, this);
    }

    @Override
    public int getTeamSize() {
        return gameManager.getGame().getSetting().getTeamSize();
    }

    @Override
    public int getFinalHeal() {
        return gameManager.getGame().getSetting().getFinalHeal();
    }

    @Override
    public int getGracePeriod() {
        return gameManager.getGame().getSetting().getGracePeriod();
    }

    @Override
    public int getBorder() {
        return gameManager.getGame().getSetting().getBorderSize();
    }

    @Override
    public int getFirstShrink() {
        return gameManager.getGame().getSetting().getFirstShrink();
    }

    @Override
    public int getNetherTime() {
        return gameManager.getGame().getSetting().getNetherTime();
    }

    @Override
    public int getAppleRate() {
        return gameManager.getGame().getSetting().getAppleRate();
    }

    @Override
    public boolean isTeamGame() {
        return gameManager.getGame().getSetting().getTeamSize() > 1;
    }

    @Override
    public Team getTeam(Player player) {
        return UHCUtils.getTeam(player);
    }

    @Override
    public Stats getPlayerStats(Player player, StatType statType) {
        return storageRegistry.getProfile(player.getUniqueId()).getStats(statType);
    }

    @Override
    public boolean isPlaying(UUID uniqueId) {
        return uniqueId != null && UHCUtils.isAlive(uniqueId);
    }

    @Override
    public boolean isSpectator(UUID uniqueId) {
        return uniqueId != null && !UHCUtils.isAlive(uniqueId);
    }

    @Override
    public CustomItemRegistry getCustomItemRegistry() {
        return customItemRegistry;
    }
}
