package me.lotiny.misty.bukkit.game;

import io.fairyproject.container.Autowired;
import lombok.Getter;
import lombok.Setter;
import me.lotiny.misty.api.game.ConfigType;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameSetting;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.api.team.TeamManager;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.Utilities;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class GameSettingImpl implements GameSetting {

    @Autowired
    private static GameManager gameManager;
    @Autowired
    private static TeamManager teamManager;

    private UUID configId;

    private String configName;
    private String savedBy;
    private String savedDate;

    private int teamSize;
    private int finalHeal;
    private int gracePeriod;
    private int borderSize;
    private int firstShrink;
    private int netherTime;
    private int appleRate;

    private boolean lastBorderFlat;
    private boolean shears;
    private boolean lateScatter;
    private boolean godApple;
    private boolean pearlDamage;
    private boolean chatBeforePvp;
    private boolean nether;
    private boolean bedBomb;

    private boolean speed1;
    private boolean speed2;
    private boolean strength1;
    private boolean strength2;
    private boolean poison;
    private boolean invisible;

    private List<String> enabledScenarios;

    private boolean def;
    private boolean loaded;

    public GameSettingImpl(UUID configId) {
        this.configId = configId;
    }

    public GameSettingImpl(GameSetting other) {
        this.configId = other.getConfigId();
        this.configName = other.getConfigName();
        this.savedBy = other.getSavedBy();
        this.savedDate = other.getSavedDate();

        this.teamSize = other.getTeamSize();
        this.finalHeal = other.getFinalHeal();
        this.gracePeriod = other.getGracePeriod();
        this.borderSize = other.getBorderSize();
        this.firstShrink = other.getFirstShrink();
        this.netherTime = other.getNetherTime();
        this.appleRate = other.getAppleRate();

        this.lastBorderFlat = other.isLastBorderFlat();
        this.shears = other.isShears();
        this.lateScatter = other.isLateScatter();
        this.godApple = other.isGodApple();
        this.pearlDamage = other.isPearlDamage();
        this.chatBeforePvp = other.isChatBeforePvp();
        this.nether = other.isNether();
        this.bedBomb = other.isBedBomb();

        this.speed1 = other.isSpeed1();
        this.speed2 = other.isSpeed2();
        this.strength1 = other.isStrength1();
        this.strength2 = other.isStrength2();
        this.poison = other.isPoison();
        this.invisible = other.isInvisible();

        this.enabledScenarios = other.getEnabledScenarios() != null
                ? List.copyOf(other.getEnabledScenarios())
                : null;

        this.def = other.isDef();
        this.loaded = other.isLoaded();
    }

    @Override
    public void setMaxTeamSize(int size) {
        if (size < gameManager.getGame().getSetting().getTeamSize()) {
            List<Team> teamsToDisband = new ArrayList<>();
            teamManager.getTeams().forEach((id, team) -> {
                if (team.getMembers(false).size() > size) {
                    team.sendMessage("&cThe max team size has been change to lower than your currently team size so your team has been disbanded.");
                    teamsToDisband.add(team);
                }
            });

            teamsToDisband.forEach(teamManager::deleteTeam);
        }

        gameManager.getGame().getSetting().setTeamSize(size);
    }

    @Override
    public void setConfig(ConfigType configType, Object value, @Nullable CommandSender sender) {
        configType.apply(this, value);
        if (sender != null) {
            String message;
            if (value instanceof Boolean b) {
                message = b ? Message.CONFIG_CHANGED_ENABLED.toString()
                        : Message.CONFIG_CHANGED_DISABLED.toString();
            } else {
                message = Message.CONFIG_CHANGED.toString()
                        .replace("<value>", value.toString());
            }

            Utilities.broadcast(message
                    .replace("<config>", Utilities.getFormattedName(configType.name()))
                    .replace("<player>", sender.getName()));
        }
    }
}
