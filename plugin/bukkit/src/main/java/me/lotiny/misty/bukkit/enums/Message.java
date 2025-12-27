package me.lotiny.misty.bukkit.enums;

import io.fairyproject.util.CC;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.bukkit.config.Config;

@RequiredArgsConstructor
public enum Message {

    SET_SPAWN(Config.getMessageConfig().getSetSpawn()),
    CLEAR_LAG(Config.getMessageConfig().getClearLag()),
    WRONG_STATE(Config.getMessageConfig().getWrongState()),
    HEALTH(Config.getMessageConfig().getHealth()),

    LOGIN_DISALLOW_WHITELIST(Config.getMessageConfig().getLoginDisallowWhitelist()),
    LOGIN_DISALLOW_SCATTER(Config.getMessageConfig().getLoginDisallowScatter()),
    LOGIN_DISALLOW_END(Config.getMessageConfig().getLoginDisallowEnd()),

    CONFIG_CHANGED(Config.getMessageConfig().getConfigChanged()),
    CONFIG_CHANGED_ENABLED(Config.getMessageConfig().getConfigChangedEnabled()),
    CONFIG_CHANGED_DISABLED(Config.getMessageConfig().getConfigChangedDisabled()),
    CONFIG_EDIT_NAME(Config.getMessageConfig().getConfigEditName()),

    SCHEDULE_NO_SCHEDULE(Config.getMessageConfig().getScheduleNoSchedule()),
    SCHEDULE_CANCEL(Config.getMessageConfig().getScheduleCancel()),
    SCHEDULE_ALREADY_SET(Config.getMessageConfig().getScheduleAlreadySet()),
    SCHEDULE_SET(Config.getMessageConfig().getScheduleSet()),
    SCHEDULE_MINIMUM(Config.getMessageConfig().getScheduleMinimum()),

    HOST_ALREADY_HAVE_HOST(Config.getMessageConfig().getHostAlreadyHaveHost()),
    HOST_SET_HOST(Config.getMessageConfig().getHostSetHost()),
    HOST_REMOVE_HOST(Config.getMessageConfig().getHostRemoveHost()),

    GRACE_PERIOD_TIME(Config.getMessageConfig().getGracePeriodTime()),
    FINAL_HEAL_TIME(Config.getMessageConfig().getFinalHealTime()),
    REBOOT_TIME(Config.getMessageConfig().getRebootTime()),
    SCATTER_TIME(Config.getMessageConfig().getScatterTime()),
    SCATTER_FINISHED(Config.getMessageConfig().getScatterFinished()),
    START_TIME(Config.getMessageConfig().getStartTime()),
    GAME_STATED(Config.getMessageConfig().getGameStarted()),

    LATE_SCATTER_DISABLED(Config.getMessageConfig().getLateScatterDisabled()),
    LATE_SCATTER_CANT(Config.getMessageConfig().getLateScatterCant()),

    RE_SCATTER_DONE(Config.getMessageConfig().getReScatterDone()),
    RE_SCATTER_CANT(Config.getMessageConfig().getReScatterCant()),
    RE_SCATTER_LIMITED(Config.getMessageConfig().getReScatterLimited()),

    RESPAWN_DONE(Config.getMessageConfig().getRespawnDone()),
    RESPAWN_CANT(Config.getMessageConfig().getRespawnCant()),

    WHITELIST_ON(Config.getMessageConfig().getWhitelistOn()),
    WHITELIST_OFF(Config.getMessageConfig().getWhitelistOff()),
    WHITELIST_EMPTY(Config.getMessageConfig().getWhitelistEmpty()),
    WHITELIST_PLAYER_NOT_WHITELISTED(Config.getMessageConfig().getWhitelistPlayerNotWhitelisted()),
    WHITELIST_PLAYER_ALREADY_WHITELISTED(Config.getMessageConfig().getWhitelistPlayerAlreadyWhitelisted()),
    WHITELIST_ADD(Config.getMessageConfig().getWhitelistAdd()),
    WHITELIST_REMOVE(Config.getMessageConfig().getWhitelistRemove()),
    WHITELIST_CANCEL(Config.getMessageConfig().getWhitelistCancel()),
    WHITELIST_NAME_NOT_VALID(Config.getMessageConfig().getWhitelistNameNotValid()),

    BORDER_FORCE_SHRINK_INVALID(Config.getMessageConfig().getBorderForceShrinkInvalid()),
    BORDER_FORCE_SHRINK_SHRUNK(Config.getMessageConfig().getBorderForceShrinkShrunk()),
    BORDER_SHRINKING_TIME(Config.getMessageConfig().getBorderShrinkingTime()),
    BORDER_SHRUNK(Config.getMessageConfig().getBorderShrunk()),

    TEAM_NOT_FOUND(Config.getMessageConfig().getTeamNotFound()),
    TEAM_NOT_IN_TEAM(Config.getMessageConfig().getTeamNotInTeam()),
    TEAM_INVITE_CANT(Config.getMessageConfig().getTeamInviteCant()),
    TEAM_INVITE_SEND(Config.getMessageConfig().getTeamInviteSend()),
    TEAM_INVITE_RECEIVED(Config.getMessageConfig().getTeamInviteReceived()),
    TEAM_INVITE_ALREADY_SEND(Config.getMessageConfig().getTeamInviteAlreadySend()),
    TEAM_JOIN_FAILED(Config.getMessageConfig().getTeamJoinFailed()),
    TEAM_DISABLED(Config.getMessageConfig().getTeamDisabled()),
    TEAM_FULL(Config.getMessageConfig().getTeamFull()),
    TEAM_ALREADY_IN_TEAM(Config.getMessageConfig().getTeamAlreadyInTeam()),
    TEAM_CREATE(Config.getMessageConfig().getTeamCreate()),
    TEAM_RANDOM_ENABLED(Config.getMessageConfig().getTeamRandomEnabled()),
    TEAM_RANDOM_DISABLED(Config.getMessageConfig().getTeamRandomDisabled()),
    TEAM_MEMBER_JOINED(Config.getMessageConfig().getTeamMemberJoined()),
    TEAM_MEMBER_LEFT(Config.getMessageConfig().getTeamMemberLeft()),
    TEAM_SEND_COORDS(Config.getMessageConfig().getTeamSendCoords()),
    TEAM_TOGGLE_TEAMCHAT_ENABLED(Config.getMessageConfig().getTeamToggleTeamChatEnabled()),
    TEAM_TOGGLE_TEAMCHAT_DISABLED(Config.getMessageConfig().getTeamToggleTeamChatDisabled()),

    PRACTICE_DISABLED(Config.getMessageConfig().getPracticeDisabled()),
    PRACTICE_ENABLED(Config.getMessageConfig().getPracticeEnabled()),
    PRACTICE_FULL(Config.getMessageConfig().getPracticeFull()),
    PRACTICE_SET_MAX_PLAYERS(Config.getMessageConfig().getPracticeSetMaxPlayers()),
    PRACTICE_SET_KIT(Config.getMessageConfig().getPracticeSetKit()),
    PRACTICE_SET_LOCATION(Config.getMessageConfig().getPracticeSetLocation()),
    PRACTICE_IS_DISABLED(Config.getMessageConfig().getPracticeIsDisabled()),

    HOLOGRAM_CREATE(Config.getMessageConfig().getHologramCreate()),
    HOLOGRAM_DELETE(Config.getMessageConfig().getHologramDelete()),

    CHAT_DISABLED(Config.getMessageConfig().getChatDisabled()),
    CHAT_MUTE(Config.getMessageConfig().getChatMute()),
    CHAT_ALREADY_MUTE(Config.getMessageConfig().getChatAlreadyMute()),
    CHAT_UNMUTE(Config.getMessageConfig().getChatUnmute()),
    CHAT_ALREADY_UNMUTE(Config.getMessageConfig().getChatAlreadyUnmute()),

    DATA_DECREASED(Config.getMessageConfig().getDataDecreased()),
    DATA_INCREASED(Config.getMessageConfig().getDataIncreased()),
    DATA_SET(Config.getMessageConfig().getDataSet()),
    DATA_RESET(Config.getMessageConfig().getDataReset()),

    BED_BOMB_DISABLED(Config.getMessageConfig().getBedBombDisabled()),

    // Scenarios
    SCENARIO_ENABLED(Config.getMessageConfig().getScenarioEnabled()),
    SCENARIO_DISABLED(Config.getMessageConfig().getScenarioDisabled()),
    SCENARIO_NOT_ENABLED(Config.getMessageConfig().getScenarioNotEnabled()),
    SCENARIO_BLOCK_ACTION(Config.getMessageConfig().getScenarioBlockAction()),

    LOVE_AT_FIRST_SIGHT_TEAM_WITH(Config.getMessageConfig().getLoveAtFirstSightTeamWith()),
    LOVE_AT_FIRST_SIGHT_ALREADY_HAVE_TEAM(Config.getMessageConfig().getLoveAtFirstSightAlreadyHaveTeam()),

    ULTRA_PARANOID_BROADCAST(Config.getMessageConfig().getUltraParanoidBroadcast()),

    TIMEBOMB_EXPLODE(Config.getMessageConfig().getTimebombExplode()),

    SAFELOOT_LOCKED(Config.getMessageConfig().getSafelootLocked()),

    BLOCK_RUSH_FIRST(Config.getMessageConfig().getBlockRushFirst()),

    BETTER_ENCHANT_USED(Config.getMessageConfig().getBetterEnchantUsed()),

    DO_NOT_DISTURB_NOT_LINKED_TO(Config.getMessageConfig().getDoNotDisturbNotLinkedTo()),
    DO_NOT_DISTURB_LINKED_WITH(Config.getMessageConfig().getDoNotDisturbLinkedWith()),
    DO_NOT_DISTURB_UNLINKED_WITH(Config.getMessageConfig().getDoNotDisturbUnlinkedWith()),

    RVB_RESET_CAPTAINS(Config.getMessageConfig().getRvbResetCaptains()),
    RVB_ASSIGN_CAPTAIN_RED(Config.getMessageConfig().getRvbAssignCaptainRed()),
    RVB_ASSIGN_CAPTAIN_BLUE(Config.getMessageConfig().getRvbAssignCaptainBlue()),
    RVB_ALREADY_HAVE_CAPTAIN_RED(Config.getMessageConfig().getRvbAlreadyHaveCaptainRed()),
    RVB_ALREADY_HAVE_CAPTAIN_BLUE(Config.getMessageConfig().getRvbAlreadyHaveCaptainBlue()),

    BATS_LUCKY(Config.getMessageConfig().getBatsLucky()),
    BATS_UNLUCKY(Config.getMessageConfig().getBatsUnlucky()),

    ARCANE_ARCHIVES_DROP(Config.getMessageConfig().getArcaneArchivesDrop()),

    FORBIDDEN_ALCHEMY_DROP(Config.getMessageConfig().getForbiddenAlchemyDrop()),

    NO_CLEAN_APPLIED(Config.getMessageConfig().getNoCleanApplied()),
    NO_CLEAN_EXPIRED(Config.getMessageConfig().getNoCleanExpired()),
    NO_CLEAN_PROTECTED(Config.getMessageConfig().getNoCleanProtected()),

    WEB_LIMIT_REACHED(Config.getMessageConfig().getWebLimitReached()),

    ENTROPY_LEVEL(Config.getMessageConfig().getEntropyLevel()),
    ENTROPY_DEAD(Config.getMessageConfig().getEntropyDead()),

    CHUMP_CHARITY_BROADCAST(Config.getMessageConfig().getChumpCharityBroadcast()),
    CHUMP_CHARITY_PLAYER(Config.getMessageConfig().getChumpCharityPlayer()),

    PLAYER_SWAP_PLAYER(Config.getMessageConfig().getPlayerSwapPlayer()),
    PLAYER_SWAP_BROADCAST(Config.getMessageConfig().getPlayerSwapBroadcast()),

    AUTOSTART_ANNOUNCE_MESSAGE(Config.getMainConfig().getAutoStart().getAnnounce().getMessage());

    private final String text;

    @Override
    public String toString() {
        return CC.translate(text.replace("<prefix>", Config.getMessageConfig().getPrefix()));
    }
}