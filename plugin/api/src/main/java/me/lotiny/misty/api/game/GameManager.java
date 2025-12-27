package me.lotiny.misty.api.game;

import io.fairyproject.util.Cooldown;
import me.lotiny.misty.api.game.registry.CombatLogger;
import me.lotiny.misty.api.game.registry.GameRegistry;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface GameManager {

    GameRegistry getRegistry();

    Cooldown<CombatLogger> getCombatLoggerCooldown();

    Map<UUID, GameSetting> getGameSettingMap();

    Game getGame();

    void loadGame(UUID configId);

    void saveGame(GameSetting gameSetting);

    void addGoldenHeadRecipe();

    void registerPlayerDeath(Player player);

    Location findSafeScatterLocation(World world, int size);

    void teleportToRandomLocation(Player player, int size);

    boolean checkWinner();

    void executeFinalHeal();

    void endGracePeriod();

    Optional<CombatLogger> findCombatLogger(Player player);

    void disqualifyPlayer(UUID uuid);
}
