package me.lotiny.misty.api.game.registry;

import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.api.task.AbstractScheduleTask;
import me.lotiny.misty.api.team.Team;
import org.bukkit.command.CommandSender;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface GameRegistry {

    Map<UUID, CombatLogger> getCombatLoggers();

    Map<UUID, Boolean> getPlayers();

    List<UUID> getPlayersToScatter();

    void setPlayersToScatter(List<UUID> playersToScatter);

    List<UUID> getPlayersScattered();

    void setPlayersScattered(List<UUID> playersScattered);

    List<String> getWhitelistPlayers();

    void setWhitelistPlayers(List<String> whitelistPlayers);

    GameState getState();

    void setState(GameState state);

    String getUhcWorld();

    void setUhcWorld(String uhcWorld);

    String getNetherWorld();

    void setNetherWorld(String netherWorld);

    boolean isClearChat();

    void setClearChat(boolean clearChat);

    boolean isWorldLoaded();

    void setWorldLoaded(boolean worldLoaded);

    boolean isChatMuted();

    void setChatMuted(boolean chatMuted);

    boolean isPvpEnabled();

    void setPvpEnabled(boolean pvpEnabled);

    boolean isDamage();

    void setDamage(boolean damage);

    boolean isFinalHealHappened();

    void setFinalHealHappened(boolean finalHealHappened);

    boolean isFirstShrunk();

    void setFirstShrunk(boolean firstShrunk);

    boolean isCanShrink();

    void setCanShrink(boolean canShrink);

    boolean isWhitelist();

    void setWhitelist(boolean whitelist);

    Profile getHost();

    void setHost(Profile host);

    Team getWinner();

    void setWinner(Team winner);

    ZoneId getZoneId();

    void setZoneId(ZoneId zoneId);

    AbstractScheduleTask getBorderTask();

    void setBorderTask(AbstractScheduleTask borderTask);

    AbstractScheduleTask getGameTask();

    void setGameTask(AbstractScheduleTask gameTask);

    AbstractScheduleTask getStartTask();

    void setStartTask(AbstractScheduleTask startTask);

    AbstractScheduleTask getLastCountdownTask();

    void setLastCountdownTask(AbstractScheduleTask lastCountdownTask);

    AbstractScheduleTask getRebootTask();

    void setRebootTask(AbstractScheduleTask rebootTask);

    List<UUID> getAlivePlayers();

    List<UUID> getSpectators();

    String getHostName();

    void setWhitelist(CommandSender sender, boolean enabled);
}
