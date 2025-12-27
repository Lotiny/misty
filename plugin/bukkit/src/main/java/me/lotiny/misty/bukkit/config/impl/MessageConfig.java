package me.lotiny.misty.bukkit.config.impl;

import de.exlll.configlib.Configuration;
import lombok.Getter;
import me.lotiny.misty.bukkit.config.BaseConfig;

@Getter
@Configuration
public final class MessageConfig extends BaseConfig {

    private String prefix = "&7[&b&lUHC&7]";

    private String setSpawn = "<prefix> &fYou’ve set this location as the &bLobby Location&f.";
    private String clearLag = "<prefix> &aSuccessfully removed all entities in the <world> world.";
    private String wrongState = "<prefix> &cYou can’t do that in this state.";
    private String health = "<prefix> &b<target>&a’s health: &c<health>&4❤";

    private String loginDisallowWhitelist = "&cThe server is currently whitelisted.";
    private String loginDisallowScatter = "&cScatter is currently happening. Try again later!";
    private String loginDisallowEnd = "&cThe game has already ended.";

    private String configChanged = "<prefix> &b<config>&e has been set to &b<value>&e by &b<player>";
    private String configChangedEnabled = "<prefix> &b<config>&e has been &aenabled &eby &b<player>";
    private String configChangedDisabled = "<prefix> &b<config>&e has been &cdisabled &eby &b<player>";
    private String configEditName = "<prefix> &aChanged this config name to &r<name>";

    private String scheduleNoSchedule = "<prefix> &cThe countdown hasn’t been set yet. Use '/uhc schedule <time>' to set it.";
    private String scheduleCancel = "<prefix> &cYou’ve canceled the countdown.";
    private String scheduleAlreadySet = "<prefix> &cThe countdown is already running. Type '/uhc schedule cancel' to stop it.";
    private String scheduleSet = "<prefix> &eYou’ve set the countdown to &b<time>&e.";
    private String scheduleMinimum = "<prefix> &cThe start time must be greater than 10 seconds.";

    private String hostAlreadyHaveHost = "<prefix> &eThe host for this game is already set to &b<host>&e.";
    private String hostSetHost = "<prefix> &eYou’ve set &b<target> &eas the host.";
    private String hostRemoveHost = "<prefix> &cYou’ve removed &c&l<target> &cfrom being the host.";

    private String gracePeriodTime = "<prefix> &fGrace Period end in &b<time>&f.";
    private String finalHealTime = "<prefix> &fFinal Heal in &b<time>&f.";
    private String rebootTime = "<prefix> &cThe server will shutdown in &4&l<time>&c.";
    private String scatterTime = "<prefix> &eScattering starts in &b<time>&e.";
    private String scatterFinished = "<prefix> &aFinished scatter all players!";
    private String startTime = "<prefix> &eThe game starts in &b<time>&e.";
    private String gameStarted = "<prefix> &eThe game has started. Good Luck!";

    private String lateScatterDisabled = "<prefix> &cLate-scatter is currently disabled.";
    private String lateScatterCant = "<prefix> &cYou can’t late-scatter this player.";

    private String reScatterDone = "<prefix> &aYou’ve been re-scattered! You can use re-scatter &b<amount> &amore times.";
    private String reScatterCant = "<prefix> &cYou can no longer re-scatter.";
    private String reScatterLimited = "<prefix> &cYou have reached your re-scatter limit of &b<amount>&c.";

    private String respawnDone = "<prefix> &b<player> &ehas been respawned.";
    private String respawnCant = "<prefix> &cThis player can’t be respawned.";

    private String whitelistOn = "<prefix> &eWhitelist has been turned &aon &eby &b<player>&e.";
    private String whitelistOff = "<prefix> &eWhitelist has been turned &coff &eby &b<player>&e.";
    private String whitelistEmpty = "<prefix> &cNobody is whitelisted.";
    private String whitelistPlayerNotWhitelisted = "<prefix> &c<player> is not whitelisted.";
    private String whitelistPlayerAlreadyWhitelisted = "<prefix> &c<player> is already whitelisted.";
    private String whitelistAdd = "<prefix> &aYou’ve added &b<player>&a to the whitelist.";
    private String whitelistRemove = "<prefix> &aYou’ve removed &b<player>&a from the whitelist.";
    private String whitelistCancel = "<prefix> &cYou've cancelled adding a player to the whitelist.";
    private String whitelistNameNotValid = "<prefix> &cThis player name doesn't valid";

    private String borderForceShrinkInvalid = "<prefix> &cYou can't set the force-shrink border to this size.";
    private String borderForceShrinkShrunk = "<prefix> &aYou have force-shrunk the border to &b<size>&a.";
    private String borderShrinkingTime = "&7[&b&lBorder&7] &fThe border will shrink to &b<size> &fin &b<time>&f.";
    private String borderShrunk = "&7[&b&lBorder&7] &fThe border has shrunk to &b<size>&f.";

    private String teamNotFound = "&cTeam not found.";
    private String teamNotInTeam = "&cThis player is not in a team!";
    private String teamInviteCant = "&cThat player already has a team or is already in your team!";
    private String teamInviteSend = "&eYou've invited &b<invited>&e to join your team.";
    private String teamInviteReceived = "&eYou have been invited to join &b<inviter>&e's team.";
    private String teamInviteAlreadySend = "&cYou have already sent an invitation to this player.";
    private String teamJoinFailed = "&cJoining the team failed, either the invite expired or the player doesn't have a team.";
    private String teamDisabled = "&cTeams are not enabled or this feature has been turned off.";
    private String teamFull = "&cThe team is already full!";
    private String teamAlreadyInTeam = "&eYou are already in a team.";
    private String teamCreate = "&aYou have created a team.";
    private String teamRandomEnabled = "&aYou have enabled random teammates.";
    private String teamRandomDisabled = "&cYou have disabled random teammates.";
    private String teamMemberJoined = "&b<player> &ahas joined the team!";
    private String teamMemberLeft = "&b<player> &ehas left the team.";
    private String teamSendCoords = "&7[&bCoords&7] &b<player>&e's coordinates: &7(&b<x>, <y>, <z>&7)";
    private String teamToggleTeamChatEnabled = "&eYou have&a enabled&e team chat.";
    private String teamToggleTeamChatDisabled = "&eYou have&c disabled&e team chat.";

    private String practiceDisabled = "<prefix> &ePractice has been &cdisabled &eby &b<player>&e.";
    private String practiceEnabled = "<prefix> &ePractice has been &aenabled &eby &b<player>&e.";
    private String practiceFull = "<prefix> &cFailed to join practice — the arena is full!";
    private String practiceSetMaxPlayers = "<prefix> &eYou have updated the practice max players to &b<amount>&e.";
    private String practiceSetKit = "<prefix> &eYou have updated the practice kit to your inventory contents.";
    private String practiceSetLocation = "<prefix> &eYou have set the practice location to your current position.";
    private String practiceIsDisabled = "<prefix> &cPractice is currently disabled.";

    private String hologramCreate = "<prefix> <type> &ehologram has been created.";
    private String hologramDelete = "<prefix> <type> &ehologram has been deleted.";

    private String chatDisabled = "<prefix> &cYou cannot chat right now — chat is disabled.";
    private String chatMute = "<prefix> &cChat is now muted.";
    private String chatAlreadyMute = "<prefix> &cChat is already muted.";
    private String chatUnmute = "<prefix> &aChat is now unmuted.";
    private String chatAlreadyUnmute = "<prefix> &aChat is already unmuted.";

    private String dataDecreased = "&b<player>&e's &b<data> &edata has decreased by &b<amount>&e.";
    private String dataIncreased = "&b<player>&e's &b<data> &edata has increased by &b<amount>&e.";
    private String dataSet = "&b<player>&e's &b<data> &edata has been set to &b<amount>&e.";
    private String dataReset = "&b<player>&e's data has been reset.";

    private String bedBombDisabled = "<prefix> &cBed Bomb is currently disabled.";

    private String scenarioEnabled = "&7[&6&lScenario&7] &b<scenario> &ehas been &aenabled &eby &b<player>&e.";
    private String scenarioDisabled = "&7[&6&lScenario&7] &b<scenario> &ehas been &cdisabled &eby &b<player>&e.";
    private String scenarioNotEnabled = "<prefix> &b<scenario>&c is not enabled.";
    private String scenarioBlockAction = "<prefix> &cYou cannot do this while '<scenario>' is enabled.";

    private String loveAtFirstSightTeamWith = "&7[&dLAFS&7] &aYou are now teamed with &b<player>&a!";
    private String loveAtFirstSightAlreadyHaveTeam = "&7[&dLAFS&7] &cThat player already has a teammate.";

    private String ultraParanoidBroadcast = "&7[&6Ultra-Paranoid&7] &b<player> &ehas mined &b<block> &eat &b(<x>, <y>, <z>)";

    private String timebombExplode = "&7[&6Timebomb&7] &f<player>'s corpse has exploded.";

    private String safelootLocked = "&7[&9Safeloot&7] &cThis chest is locked!";

    private String blockRushFirst = "&7[&eBlock-Rush&7] &aYou are the first person to mine &b<block>&a!";

    private String betterEnchantUsed = "&cEnchant removed!";

    private String doNotDisturbNotLinkedTo = "&7[&dDnD&7] &cYou are not linked to this team.";
    private String doNotDisturbLinkedWith = "&7[&dDnD&7] &eYou are now linked with &b<linked>&e.";
    private String doNotDisturbUnlinkedWith = "&7[&dDnD&7] &eYou are now unlinked from &b<linked>&e.";

    private String rvbResetCaptains = "<prefix> &cTeam captains have been reset.";
    private String rvbAssignCaptainRed = "<prefix> &aAssigned &b<player>&a as the &cRED&a captain.";
    private String rvbAssignCaptainBlue = "<prefix> &aAssigned &b<player>&a as the &9BLUE&a captain.";
    private String rvbAlreadyHaveCaptainRed = "<prefix> &cRED &ateam already has a captain.";
    private String rvbAlreadyHaveCaptainBlue = "<prefix> &9BLUE &ateam already has a captain.";

    private String batsLucky = "&aThe bat has dropped the Golden Apple!";
    private String batsUnlucky = "&cUnlucky for you, you have died by killing the bat.";

    private String arcaneArchivesDrop = "&aYou have received &b1x<item>&a from mining Lapis Lazuli Ore.";

    private String forbiddenAlchemyDrop = "&aYou have received &b1x<item>&a from mining Redstone Ore.";

    private String noCleanApplied = "&aYou are now protected by No-Clean for 20 seconds.";
    private String noCleanExpired = "&cYou are no longer protected by No-Clean!";
    private String noCleanProtected = "&cThis player is protected by No-Clean.";

    private String webLimitReached = "&cYou have too many webs in your inventory! They have been removed to the limit of 8.";

    private String entropyLevel = "&7[&8Entropy&7] &cYou’ve lost 1 level.";
    private String entropyDead = "&7[&8Entropy&7] &cYou died because you had no levels left.";

    private String chumpCharityBroadcast = "&7[&dChump-Charity&7] &b<player> &ahas been healed by the Chump Charity scenario!";
    private String chumpCharityPlayer = "&7[&dChump-Charity&7] &eYou’ve been healed!";

    private String playerSwapPlayer = "&7[&aPlayer-Swap&7] &eYou were chosen to swap locations with &b<player>&e.";
    private String playerSwapBroadcast = "&7[&aPlayer-Swap&7] &b<player1> &eand &b<player2> &ehave swapped locations!";

}
