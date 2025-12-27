package me.lotiny.misty.bukkit.hook.impl.placeholderapi;

import io.fairyproject.container.Containers;
import io.fairyproject.util.CC;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.profile.stats.StatType;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.impl.MainConfig;
import me.lotiny.misty.bukkit.hook.rank.RankManager;
import me.lotiny.misty.bukkit.manager.leaderboard.Leaderboard;
import me.lotiny.misty.bukkit.manager.leaderboard.LeaderboardManager;
import me.lotiny.misty.bukkit.manager.leaderboard.LeaderboardPlayer;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import me.lotiny.misty.bukkit.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MistyPlaceholderAPIExpansion extends PlaceholderExpansion {

    private final ScenarioManager scenarioManager;
    private final GameManager gameManager;
    private final LeaderboardManager leaderboardManager;
    private final RankManager rankManager;

    public MistyPlaceholderAPIExpansion() {
        this.scenarioManager = Containers.get(ScenarioManager.class);
        this.gameManager = Containers.get(GameManager.class);
        this.leaderboardManager = Containers.get(LeaderboardManager.class);
        this.rankManager = Containers.get(RankManager.class);
    }

    @Override
    public @NotNull String getIdentifier() {
        return "misty";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Lotiny";
    }

    @Override
    public @NotNull String getVersion() {
        return VersionUtils.getPluginVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer == null) {
            return "Data not found.";
        }

        String[] args = params.split("_");

        // Check for a placeholder that retrieves the status (enabled/disabled) of a scenario.
        // Example: %misty_scenario_<name>%
        if (params.contains("scenario")) {
            if (args.length != 2) {
                return null;
            }

            String scenarioName = args[1];
            Scenario scenario = scenarioManager.getScenario(scenarioName);
            if (scenario == null) {
                return "Scenario not found.";
            } else {
                return (scenarioManager.isEnabled(scenarioName) ? "Enabled" : "Disabled");
            }
        }

        // Check for a placeholder that lists enabled scenarios.
        // Example: %misty_scenarios%
        if (params.equalsIgnoreCase("scenarios")) {
            return (scenarioManager.getEnabledScenarios(gameManager).isEmpty() ? "None" : scenarioManager.getEnabledScenarios(gameManager).toString());
        }

        // Check for a placeholder that retrieves the host's name.
        // Example: %misty_host%
        if (params.equalsIgnoreCase("host")) {
            return gameManager.getRegistry().getHostName();
        }

        // Check for placeholders related to leaderboards.
        if (params.contains("leaderboard")) {
            if (args.length != 4) {
                return null;
            }

            Leaderboard leaderboard = leaderboardManager.getLeaderboardMap().get(StatType.get(args[2]));
            if (leaderboard == null) {
                return "Type not found.";
            }

            try {
                int pos = Integer.parseInt(args[3]);
                LeaderboardPlayer leaderboardPlayer = leaderboard.getPlayers().get(pos);

                // Placeholder for player at a specific position in the leaderboard.
                // Example: %misty_leaderboard_player_<type>_<position>%
                if (args[1].equalsIgnoreCase("player")) {
                    return leaderboardPlayer.getName();
                }

                // Placeholder for amount at a specific position in the leaderboard.
                // Example: %misty_leaderboard_amount_<type>_<position>%
                if (args[1].equalsIgnoreCase("amount")) {
                    return String.valueOf(leaderboardPlayer.getPlace());
                }
            } catch (NumberFormatException e) {
                return "Position must be a number.";
            }
        }

        // Check for a placeholder that retrieves the player's nametags.
        // Example: %misty_nametags%
        if (params.equalsIgnoreCase("nametags")) {
            return this.getNameTag(offlinePlayer.getUniqueId());
        }

        // Check for a placeholder that retrieves the player's health
        // Example: %misty_health%
        if (params.equalsIgnoreCase("health")) {
            if (scenarioManager.isEnabled("Secret Health")) {
                return CC.MAGIC + "$$";
            } else {
                Player player = Bukkit.getPlayer(offlinePlayer.getUniqueId());
                if (player == null) {
                    return null;
                }

                return String.valueOf(Math.round(player.getHealth()));
            }
        }

        // Return null if no matching placeholder is found.
        return null;
    }

    private String getNameTag(UUID uuid) {
        MainConfig.NameTag nameTag = Config.getMainConfig().getNameTag();
        String color = rankManager.getRank().getRankColor(uuid);
        if (!UHCUtils.isAlive(uuid)) {
            color = nameTag.getSpectator();
        }

        Player player = Bukkit.getPlayer(uuid);
        Team team = UHCUtils.getTeam(player);
        boolean redVsBlue = gameManager.getRegistry().getState() != GameState.LOBBY
                && scenarioManager.isEnabled("Red vs Blue");
        if (redVsBlue && team != null) {
            color = switch (team.getId()) {
                case 0 -> "&c";
                case 1 -> "&9";
                default -> color;
            };
        }

        if (UHCUtils.hasNoClean(player)) {
            color = nameTag.getNoClean();
        }

        if (team != null && scenarioManager.isEnabled("Do Not Disturb") && UHCUtils.isInCombat(team)) {
            color = nameTag.getDoNotDisturb();
        }

        if (team != null && gameManager.getGame().getSetting().getTeamSize() > 1 && !redVsBlue) {
            return color + nameTag.getTeamPrefix().replace("<team>", String.valueOf(team.getId()));
        } else {
            return color;
        }
    }
}
