package me.lotiny.misty.bukkit.provider;

import io.fairyproject.bukkit.util.LegacyAdventureUtil;
import io.fairyproject.container.Autowired;
import io.fairyproject.container.DependsOn;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.mc.MCPlayer;
import io.fairyproject.mc.nametag.NameTag;
import io.fairyproject.mc.nametag.NameTagAdapter;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.ConfigManager;
import me.lotiny.misty.bukkit.config.impl.MainConfig;
import me.lotiny.misty.bukkit.hook.rank.RankManager;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import me.lotiny.misty.bukkit.utils.VersionUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

@DependsOn(ConfigManager.class)
@InjectableComponent
public class NameTagProvider extends NameTagAdapter {

    @Autowired
    private static GameManager gameManager;
    @Autowired
    private static RankManager rankManager;
    @Autowired
    private static ScenarioManager scenarioManager;

    public NameTagProvider() {
        super("misty-nametag", 100);
    }

    @Override
    public NameTag fetch(MCPlayer player, MCPlayer target) {
        if (player == null || target == null) {
            return NameTag.builder().build();
        }

        Player targetPlayer = target.as(Player.class);
        Player sourcePlayer = player.as(Player.class);

        String color = determineColor(sourcePlayer, targetPlayer);
        String prefix = "";

        boolean isTeamGame = gameManager.getGame().getSetting().getTeamSize() > 1;
        Team team = UHCUtils.getTeam(targetPlayer);

        if (isTeamGame && team != null && !scenarioManager.isEnabled("Red vs Blue")) {
            prefix = Config.getMainConfig().getNameTag().getTeamPrefix().replace("<team>", String.valueOf(team.getId()));
        } else if (VersionUtils.is(8, 8)) {
            prefix = " ";
        }

        if (VersionUtils.is(8, 8)) {
            return NameTag.builder()
                    .prefix(LegacyAdventureUtil.decode(color + prefix))
                    .build();
        }

        return NameTag.builder()
                .color(fromColorCode(color))
                .prefix(LegacyAdventureUtil.decode(prefix))
                .build();
    }

    private String determineColor(Player player, Player target) {
        MainConfig config = Config.getMainConfig();
        if (!UHCUtils.isAlive(target.getUniqueId())) {
            return config.getNameTag().getSpectator();
        }

        Team targetTeam = UHCUtils.getTeam(target);
        Team profileTeam = UHCUtils.getTeam(player);
        if (gameManager.getRegistry().getState() != GameState.INGAME) {
            return rankManager.getRank().getRankColor(target.getUniqueId());
        }

        if (UHCUtils.hasNoClean(target)) {
            return config.getNameTag().getNoClean();
        }

        if (scenarioManager.isEnabled("Red vs Blue")) {
            if (targetTeam.getId() == 0) {
                return "&c";
            } else {
                return "&9";
            }
        }

        boolean targetInCombat = UHCUtils.isInCombat(targetTeam);
        boolean profileInCombat = UHCUtils.isInCombat(profileTeam);
        boolean combatWith = UHCUtils.isCombatWith(targetTeam, profileTeam);
        if (scenarioManager.isEnabled("Do Not Disturb") && targetInCombat && (!profileInCombat || combatWith)) {
            return config.getNameTag().getDoNotDisturb();
        }

        if (targetTeam.isSame(profileTeam)) {
            return config.getNameTag().getFriendly();
        } else {
            return config.getNameTag().getEnemy();
        }
    }

    public TextColor fromColorCode(String colorCode) {
        if (colorCode == null || colorCode.length() < 2 || colorCode.charAt(0) != '&') {
            return null;
        }

        if (colorCode.startsWith("&#") && colorCode.length() == 8) {
            String hex = colorCode.substring(1);
            try {
                return TextColor.fromHexString(hex);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        char codeChar = Character.toLowerCase(colorCode.charAt(1));
        return switch (codeChar) {
            case '0' -> NamedTextColor.BLACK;
            case '1' -> NamedTextColor.DARK_BLUE;
            case '2' -> NamedTextColor.DARK_GREEN;
            case '3' -> NamedTextColor.DARK_AQUA;
            case '4' -> NamedTextColor.DARK_RED;
            case '5' -> NamedTextColor.DARK_PURPLE;
            case '6' -> NamedTextColor.GOLD;
            case '7' -> NamedTextColor.GRAY;
            case '8' -> NamedTextColor.DARK_GRAY;
            case '9' -> NamedTextColor.BLUE;
            case 'a' -> NamedTextColor.GREEN;
            case 'b' -> NamedTextColor.AQUA;
            case 'c' -> NamedTextColor.RED;
            case 'd' -> NamedTextColor.LIGHT_PURPLE;
            case 'e' -> NamedTextColor.YELLOW;
            case 'f' -> NamedTextColor.WHITE;
            default -> null;
        };
    }
}
