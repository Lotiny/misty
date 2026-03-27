package me.lotiny.misty.utils;

import io.fairyproject.util.CC;
import lombok.experimental.UtilityClass;
import me.lotiny.misty.config.Config;

@UtilityClass
public class Message {

    public String SET_SPAWN;
    public String CLEAR_LAG;
    public String WRONG_STATE;
    public String HEALTH;

    public String LOGIN_DISALLOW_WHITELIST;
    public String LOGIN_DISALLOW_SCATTER;
    public String LOGIN_DISALLOW_END;

    public String CONFIG_CHANGED;
    public String CONFIG_CHANGED_ENABLED;
    public String CONFIG_CHANGED_DISABLED;
    public String CONFIG_EDIT_NAME;

    public String SCHEDULE_NO_SCHEDULE;
    public String SCHEDULE_CANCEL;
    public String SCHEDULE_ALREADY_SET;
    public String SCHEDULE_SET;
    public String SCHEDULE_MINIMUM;

    public String HOST_ALREADY_HAVE_HOST;
    public String HOST_SET_HOST;
    public String HOST_REMOVE_HOST;

    public String GRACE_PERIOD_TIME;
    public String FINAL_HEAL_TIME;
    public String REBOOT_TIME;
    public String SCATTER_TIME;
    public String SCATTER_FINISHED;
    public String START_TIME;
    public String GAME_STATED;

    public String LATE_SCATTER_DISABLED;
    public String LATE_SCATTER_CANT;

    public String RE_SCATTER_DONE;
    public String RE_SCATTER_CANT;
    public String RE_SCATTER_LIMITED;

    public String RESPAWN_DONE;
    public String RESPAWN_CANT;

    public String WHITELIST_ON;
    public String WHITELIST_OFF;
    public String WHITELIST_EMPTY;
    public String WHITELIST_PLAYER_NOT_WHITELISTED;
    public String WHITELIST_PLAYER_ALREADY_WHITELISTED;
    public String WHITELIST_ADD;
    public String WHITELIST_REMOVE;
    public String WHITELIST_CANCEL;
    public String WHITELIST_NAME_NOT_VALID;

    public String BORDER_FORCE_SHRINK_INVALID;
    public String BORDER_FORCE_SHRINK_SHRUNK;
    public String BORDER_SHRINKING_TIME;
    public String BORDER_SHRUNK;

    public String TEAM_NOT_FOUND;
    public String TEAM_NOT_IN_TEAM;
    public String TEAM_INVITE_CANT;
    public String TEAM_INVITE_SEND;
    public String TEAM_INVITE_RECEIVED;
    public String TEAM_INVITE_ALREADY_SEND;
    public String TEAM_JOIN_FAILED;
    public String TEAM_DISABLED;
    public String TEAM_FULL;
    public String TEAM_ALREADY_IN_TEAM;
    public String TEAM_CREATE;
    public String TEAM_RANDOM_ENABLED;
    public String TEAM_RANDOM_DISABLED;
    public String TEAM_MEMBER_JOINED;
    public String TEAM_MEMBER_LEFT;
    public String TEAM_SEND_COORDS;
    public String TEAM_TOGGLE_TEAMCHAT_ENABLED;
    public String TEAM_TOGGLE_TEAMCHAT_DISABLED;

    public String PRACTICE_DISABLED;
    public String PRACTICE_ENABLED;
    public String PRACTICE_FULL;
    public String PRACTICE_SET_MAX_PLAYERS;
    public String PRACTICE_SET_KIT;
    public String PRACTICE_SET_LOCATION;
    public String PRACTICE_IS_DISABLED;

    public String HOLOGRAM_CREATE;
    public String HOLOGRAM_DELETE;

    public String CHAT_DISABLED;
    public String CHAT_MUTE;
    public String CHAT_ALREADY_MUTE;
    public String CHAT_UNMUTE;
    public String CHAT_ALREADY_UNMUTE;

    public String DATA_DECREASED;
    public String DATA_INCREASED;
    public String DATA_SET;
    public String DATA_RESET;

    public String BED_BOMB_DISABLED;

    public String SCENARIO_ENABLED;
    public String SCENARIO_DISABLED;
    public String SCENARIO_NOT_ENABLED;
    public String SCENARIO_BLOCK_ACTION;

    public String LOVE_AT_FIRST_SIGHT_TEAM_WITH;
    public String LOVE_AT_FIRST_SIGHT_ALREADY_HAVE_TEAM;

    public String ULTRA_PARANOID_BROADCAST;

    public String TIMEBOMB_EXPLODE;

    public String SAFELOOT_LOCKED;

    public String BLOCK_RUSH_FIRST;

    public String BETTER_ENCHANT_USED;

    public String DO_NOT_DISTURB_NOT_LINKED_TO;
    public String DO_NOT_DISTURB_LINKED_WITH;
    public String DO_NOT_DISTURB_UNLINKED_WITH;

    public String RVB_RESET_CAPTAINS;
    public String RVB_ASSIGN_CAPTAIN_RED;
    public String RVB_ASSIGN_CAPTAIN_BLUE;
    public String RVB_ALREADY_HAVE_CAPTAIN_RED;
    public String RVB_ALREADY_HAVE_CAPTAIN_BLUE;

    public String BATS_LUCKY;
    public String BATS_UNLUCKY;

    public String ARCANE_ARCHIVES_DROP;

    public String FORBIDDEN_ALCHEMY_DROP;

    public String NO_CLEAN_APPLIED;
    public String NO_CLEAN_EXPIRED;
    public String NO_CLEAN_PROTECTED;

    public String WEB_LIMIT_REACHED;

    public String ENTROPY_LEVEL;
    public String ENTROPY_DEAD;

    public String CHUMP_CHARITY_BROADCAST;
    public String CHUMP_CHARITY_PLAYER;

    public String PLAYER_SWAP_PLAYER;
    public String PLAYER_SWAP_BROADCAST;

    public String AUTOSTART_ANNOUNCE_MESSAGE;

    static {
        String prefix = Config.getMessageConfig().getPrefix();

        SET_SPAWN = format(Config.getMessageConfig().getSetSpawn(), prefix);
        CLEAR_LAG = format(Config.getMessageConfig().getClearLag(), prefix);
        WRONG_STATE = format(Config.getMessageConfig().getWrongState(), prefix);
        HEALTH = format(Config.getMessageConfig().getHealth(), prefix);

        LOGIN_DISALLOW_WHITELIST = format(Config.getMessageConfig().getLoginDisallowWhitelist(), prefix);
        LOGIN_DISALLOW_SCATTER = format(Config.getMessageConfig().getLoginDisallowScatter(), prefix);
        LOGIN_DISALLOW_END = format(Config.getMessageConfig().getLoginDisallowEnd(), prefix);

        CONFIG_CHANGED = format(Config.getMessageConfig().getConfigChanged(), prefix);
        CONFIG_CHANGED_ENABLED = format(Config.getMessageConfig().getConfigChangedEnabled(), prefix);
        CONFIG_CHANGED_DISABLED = format(Config.getMessageConfig().getConfigChangedDisabled(), prefix);
        CONFIG_EDIT_NAME = format(Config.getMessageConfig().getConfigEditName(), prefix);

        SCHEDULE_NO_SCHEDULE = format(Config.getMessageConfig().getScheduleNoSchedule(), prefix);
        SCHEDULE_CANCEL = format(Config.getMessageConfig().getScheduleCancel(), prefix);
        SCHEDULE_ALREADY_SET = format(Config.getMessageConfig().getScheduleAlreadySet(), prefix);
        SCHEDULE_SET = format(Config.getMessageConfig().getScheduleSet(), prefix);
        SCHEDULE_MINIMUM = format(Config.getMessageConfig().getScheduleMinimum(), prefix);

        HOST_ALREADY_HAVE_HOST = format(Config.getMessageConfig().getHostAlreadyHaveHost(), prefix);
        HOST_SET_HOST = format(Config.getMessageConfig().getHostSetHost(), prefix);
        HOST_REMOVE_HOST = format(Config.getMessageConfig().getHostRemoveHost(), prefix);

        GRACE_PERIOD_TIME = format(Config.getMessageConfig().getGracePeriodTime(), prefix);
        FINAL_HEAL_TIME = format(Config.getMessageConfig().getFinalHealTime(), prefix);
        REBOOT_TIME = format(Config.getMessageConfig().getRebootTime(), prefix);
        SCATTER_TIME = format(Config.getMessageConfig().getScatterTime(), prefix);
        SCATTER_FINISHED = format(Config.getMessageConfig().getScatterFinished(), prefix);
        START_TIME = format(Config.getMessageConfig().getStartTime(), prefix);
        GAME_STATED = format(Config.getMessageConfig().getGameStarted(), prefix);

        LATE_SCATTER_DISABLED = format(Config.getMessageConfig().getLateScatterDisabled(), prefix);
        LATE_SCATTER_CANT = format(Config.getMessageConfig().getLateScatterCant(), prefix);

        RE_SCATTER_DONE = format(Config.getMessageConfig().getReScatterDone(), prefix);
        RE_SCATTER_CANT = format(Config.getMessageConfig().getReScatterCant(), prefix);
        RE_SCATTER_LIMITED = format(Config.getMessageConfig().getReScatterLimited(), prefix);

        RESPAWN_DONE = format(Config.getMessageConfig().getRespawnDone(), prefix);
        RESPAWN_CANT = format(Config.getMessageConfig().getRespawnCant(), prefix);

        WHITELIST_ON = format(Config.getMessageConfig().getWhitelistOn(), prefix);
        WHITELIST_OFF = format(Config.getMessageConfig().getWhitelistOff(), prefix);
        WHITELIST_EMPTY = format(Config.getMessageConfig().getWhitelistEmpty(), prefix);
        WHITELIST_PLAYER_NOT_WHITELISTED = format(Config.getMessageConfig().getWhitelistPlayerNotWhitelisted(), prefix);
        WHITELIST_PLAYER_ALREADY_WHITELISTED =
                format(Config.getMessageConfig().getWhitelistPlayerAlreadyWhitelisted(), prefix);
        WHITELIST_ADD = format(Config.getMessageConfig().getWhitelistAdd(), prefix);
        WHITELIST_REMOVE = format(Config.getMessageConfig().getWhitelistRemove(), prefix);
        WHITELIST_CANCEL = format(Config.getMessageConfig().getWhitelistCancel(), prefix);
        WHITELIST_NAME_NOT_VALID = format(Config.getMessageConfig().getWhitelistNameNotValid(), prefix);

        BORDER_FORCE_SHRINK_INVALID = format(Config.getMessageConfig().getBorderForceShrinkInvalid(), prefix);
        BORDER_FORCE_SHRINK_SHRUNK = format(Config.getMessageConfig().getBorderForceShrinkShrunk(), prefix);
        BORDER_SHRINKING_TIME = format(Config.getMessageConfig().getBorderShrinkingTime(), prefix);
        BORDER_SHRUNK = format(Config.getMessageConfig().getBorderShrunk(), prefix);

        TEAM_NOT_FOUND = format(Config.getMessageConfig().getTeamNotFound(), prefix);
        TEAM_NOT_IN_TEAM = format(Config.getMessageConfig().getTeamNotInTeam(), prefix);
        TEAM_INVITE_CANT = format(Config.getMessageConfig().getTeamInviteCant(), prefix);
        TEAM_INVITE_SEND = format(Config.getMessageConfig().getTeamInviteSend(), prefix);
        TEAM_INVITE_RECEIVED = format(Config.getMessageConfig().getTeamInviteReceived(), prefix);
        TEAM_INVITE_ALREADY_SEND = format(Config.getMessageConfig().getTeamInviteAlreadySend(), prefix);
        TEAM_JOIN_FAILED = format(Config.getMessageConfig().getTeamJoinFailed(), prefix);
        TEAM_DISABLED = format(Config.getMessageConfig().getTeamDisabled(), prefix);
        TEAM_FULL = format(Config.getMessageConfig().getTeamFull(), prefix);
        TEAM_ALREADY_IN_TEAM = format(Config.getMessageConfig().getTeamAlreadyInTeam(), prefix);
        TEAM_CREATE = format(Config.getMessageConfig().getTeamCreate(), prefix);
        TEAM_RANDOM_ENABLED = format(Config.getMessageConfig().getTeamRandomEnabled(), prefix);
        TEAM_RANDOM_DISABLED = format(Config.getMessageConfig().getTeamRandomDisabled(), prefix);
        TEAM_MEMBER_JOINED = format(Config.getMessageConfig().getTeamMemberJoined(), prefix);
        TEAM_MEMBER_LEFT = format(Config.getMessageConfig().getTeamMemberLeft(), prefix);
        TEAM_SEND_COORDS = format(Config.getMessageConfig().getTeamSendCoords(), prefix);
        TEAM_TOGGLE_TEAMCHAT_ENABLED = format(Config.getMessageConfig().getTeamToggleTeamChatEnabled(), prefix);
        TEAM_TOGGLE_TEAMCHAT_DISABLED = format(Config.getMessageConfig().getTeamToggleTeamChatDisabled(), prefix);

        PRACTICE_DISABLED = format(Config.getMessageConfig().getPracticeDisabled(), prefix);
        PRACTICE_ENABLED = format(Config.getMessageConfig().getPracticeEnabled(), prefix);
        PRACTICE_FULL = format(Config.getMessageConfig().getPracticeFull(), prefix);
        PRACTICE_SET_MAX_PLAYERS = format(Config.getMessageConfig().getPracticeSetMaxPlayers(), prefix);
        PRACTICE_SET_KIT = format(Config.getMessageConfig().getPracticeSetKit(), prefix);
        PRACTICE_SET_LOCATION = format(Config.getMessageConfig().getPracticeSetLocation(), prefix);
        PRACTICE_IS_DISABLED = format(Config.getMessageConfig().getPracticeIsDisabled(), prefix);

        HOLOGRAM_CREATE = format(Config.getMessageConfig().getHologramCreate(), prefix);
        HOLOGRAM_DELETE = format(Config.getMessageConfig().getHologramDelete(), prefix);

        CHAT_DISABLED = format(Config.getMessageConfig().getChatDisabled(), prefix);
        CHAT_MUTE = format(Config.getMessageConfig().getChatMute(), prefix);
        CHAT_ALREADY_MUTE = format(Config.getMessageConfig().getChatAlreadyMute(), prefix);
        CHAT_UNMUTE = format(Config.getMessageConfig().getChatUnmute(), prefix);
        CHAT_ALREADY_UNMUTE = format(Config.getMessageConfig().getChatAlreadyUnmute(), prefix);

        DATA_DECREASED = format(Config.getMessageConfig().getDataDecreased(), prefix);
        DATA_INCREASED = format(Config.getMessageConfig().getDataIncreased(), prefix);
        DATA_SET = format(Config.getMessageConfig().getDataSet(), prefix);
        DATA_RESET = format(Config.getMessageConfig().getDataReset(), prefix);

        BED_BOMB_DISABLED = format(Config.getMessageConfig().getBedBombDisabled(), prefix);

        SCENARIO_ENABLED = format(Config.getMessageConfig().getScenarioEnabled(), prefix);
        SCENARIO_DISABLED = format(Config.getMessageConfig().getScenarioDisabled(), prefix);
        SCENARIO_NOT_ENABLED = format(Config.getMessageConfig().getScenarioNotEnabled(), prefix);
        SCENARIO_BLOCK_ACTION = format(Config.getMessageConfig().getScenarioBlockAction(), prefix);

        LOVE_AT_FIRST_SIGHT_TEAM_WITH = format(Config.getMessageConfig().getLoveAtFirstSightTeamWith(), prefix);
        LOVE_AT_FIRST_SIGHT_ALREADY_HAVE_TEAM =
                format(Config.getMessageConfig().getLoveAtFirstSightAlreadyHaveTeam(), prefix);

        ULTRA_PARANOID_BROADCAST = format(Config.getMessageConfig().getUltraParanoidBroadcast(), prefix);

        TIMEBOMB_EXPLODE = format(Config.getMessageConfig().getTimebombExplode(), prefix);

        SAFELOOT_LOCKED = format(Config.getMessageConfig().getSafelootLocked(), prefix);

        BLOCK_RUSH_FIRST = format(Config.getMessageConfig().getBlockRushFirst(), prefix);

        BETTER_ENCHANT_USED = format(Config.getMessageConfig().getBetterEnchantUsed(), prefix);

        DO_NOT_DISTURB_NOT_LINKED_TO = format(Config.getMessageConfig().getDoNotDisturbNotLinkedTo(), prefix);
        DO_NOT_DISTURB_LINKED_WITH = format(Config.getMessageConfig().getDoNotDisturbLinkedWith(), prefix);
        DO_NOT_DISTURB_UNLINKED_WITH = format(Config.getMessageConfig().getDoNotDisturbUnlinkedWith(), prefix);

        RVB_RESET_CAPTAINS = format(Config.getMessageConfig().getRvbResetCaptains(), prefix);
        RVB_ASSIGN_CAPTAIN_RED = format(Config.getMessageConfig().getRvbAssignCaptainRed(), prefix);
        RVB_ASSIGN_CAPTAIN_BLUE = format(Config.getMessageConfig().getRvbAssignCaptainBlue(), prefix);
        RVB_ALREADY_HAVE_CAPTAIN_RED = format(Config.getMessageConfig().getRvbAlreadyHaveCaptainRed(), prefix);
        RVB_ALREADY_HAVE_CAPTAIN_BLUE = format(Config.getMessageConfig().getRvbAlreadyHaveCaptainBlue(), prefix);

        BATS_LUCKY = format(Config.getMessageConfig().getBatsLucky(), prefix);
        BATS_UNLUCKY = format(Config.getMessageConfig().getBatsUnlucky(), prefix);

        ARCANE_ARCHIVES_DROP = format(Config.getMessageConfig().getArcaneArchivesDrop(), prefix);

        FORBIDDEN_ALCHEMY_DROP = format(Config.getMessageConfig().getForbiddenAlchemyDrop(), prefix);

        NO_CLEAN_APPLIED = format(Config.getMessageConfig().getNoCleanApplied(), prefix);
        NO_CLEAN_EXPIRED = format(Config.getMessageConfig().getNoCleanExpired(), prefix);
        NO_CLEAN_PROTECTED = format(Config.getMessageConfig().getNoCleanProtected(), prefix);

        WEB_LIMIT_REACHED = format(Config.getMessageConfig().getWebLimitReached(), prefix);

        ENTROPY_LEVEL = format(Config.getMessageConfig().getEntropyLevel(), prefix);
        ENTROPY_DEAD = format(Config.getMessageConfig().getEntropyDead(), prefix);

        CHUMP_CHARITY_BROADCAST = format(Config.getMessageConfig().getChumpCharityBroadcast(), prefix);
        CHUMP_CHARITY_PLAYER = format(Config.getMessageConfig().getChumpCharityPlayer(), prefix);

        PLAYER_SWAP_PLAYER = format(Config.getMessageConfig().getPlayerSwapPlayer(), prefix);
        PLAYER_SWAP_BROADCAST = format(Config.getMessageConfig().getPlayerSwapBroadcast(), prefix);

        AUTOSTART_ANNOUNCE_MESSAGE =
                format(Config.getMainConfig().getAutoStart().getAnnounce().getMessage(), prefix);
    }

    private String format(String text, String prefix) {
        if (text == null) {
            return "";
        }

        return CC.translate(text.replace("<prefix>", prefix != null ? prefix : ""));
    }
}
