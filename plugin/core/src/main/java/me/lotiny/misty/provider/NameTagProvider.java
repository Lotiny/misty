package me.lotiny.misty.provider;

import com.github.retrooper.packetevents.util.ColorUtil;
import io.fairyproject.bukkit.util.LegacyAdventureUtil;
import io.fairyproject.container.Autowired;
import io.fairyproject.container.DependsOn;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.mc.MCPlayer;
import io.fairyproject.mc.nametag.NameTag;
import io.fairyproject.mc.nametag.NameTagAdapter;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.config.Config;
import me.lotiny.misty.config.ConfigManager;
import me.lotiny.misty.config.impl.MainConfig;
import me.lotiny.misty.hook.rank.RankManager;
import me.lotiny.misty.utils.UHCUtils;
import net.kyori.adventure.text.format.NamedTextColor;
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

    private String teamPrefix;

    public NameTagProvider() {
        super("misty-nametag", 100);
    }

    @PostInitialize
    public void onPostInit() {
        this.teamPrefix = Config.getMainConfig().getNameTag().getTeamPrefix();
    }

    @Override
    public NameTag fetch(MCPlayer player, MCPlayer target) {
        if (player == null || target == null) {
            return NameTag.builder().build();
        }

        Player targetPlayer = target.as(Player.class);
        Player sourcePlayer = player.as(Player.class);

        NamedTextColor color = determineColor(sourcePlayer, targetPlayer);
        String prefix = "";

        boolean isTeamGame = gameManager.getGame().getSetting().getTeamSize() > 1;
        Team team = UHCUtils.getTeam(targetPlayer);

        if (isTeamGame && team != null && !scenarioManager.isEnabled("Red vs Blue")) {
            prefix = teamPrefix.replace("<team>", String.valueOf(team.getId()));
        }

        return NameTag.builder()
                .color(color)
                .prefix(LegacyAdventureUtil.decode(prefix))
                .build();
    }

    private NamedTextColor determineColor(Player player, Player target) {
        MainConfig config = Config.getMainConfig();
        if (!UHCUtils.isAlive(target.getUniqueId())) {
            return config.getNameTag().getSpectator();
        }

        Team targetTeam = UHCUtils.getTeam(target);
        Team profileTeam = UHCUtils.getTeam(player);
        if (gameManager.getRegistry().getState() != GameState.INGAME) {
            String rankColor = rankManager.getRank().getRankColor(target.getUniqueId());
            return fromCode(rankColor);
        }

        if (UHCUtils.hasNoClean(target)) {
            return config.getNameTag().getNoClean();
        }

        if (scenarioManager.isEnabled("Red vs Blue")) {
            if (targetTeam.getId() == 0) {
                return NamedTextColor.RED;
            } else {
                return NamedTextColor.BLUE;
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

    private NamedTextColor fromCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }

        char c = Character.toLowerCase(code.charAt(code.length() - 1));

        String codes = "0123456789abcdef";
        int id = codes.indexOf(c);

        return ColorUtil.fromId(id);
    }
}
