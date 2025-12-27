package me.lotiny.misty.bukkit.game.registry;

import lombok.Getter;
import lombok.Setter;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.game.registry.CombatLogger;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.api.task.AbstractScheduleTask;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.bukkit.config.impl.MainConfig;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class GameRegistryImpl implements GameRegistry {

    private final Map<UUID, CombatLogger> combatLoggers = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> players = new HashMap<>();

    private List<UUID> playersToScatter = new ArrayList<>();
    private List<UUID> playersScattered = new ArrayList<>();
    private List<String> whitelistPlayers;

    private GameState state;

    private String uhcWorld;
    private String netherWorld;

    private boolean clearChat;
    private boolean worldLoaded;
    private boolean chatMuted;
    private boolean pvpEnabled;
    private boolean damage;
    private boolean finalHealHappened;
    private boolean firstShrunk;
    private boolean canShrink;
    private boolean whitelist;

    private Profile host;
    private Team winner;

    private ZoneId zoneId;

    private AbstractScheduleTask borderTask;
    private AbstractScheduleTask gameTask;
    private AbstractScheduleTask startTask;
    private AbstractScheduleTask lastCountdownTask;
    private AbstractScheduleTask rebootTask;

    public GameRegistryImpl(MainConfig config) {
        this.state = GameState.LOBBY;

        this.uhcWorld = config.getWorld().getGame();
        this.netherWorld = config.getWorld().getNether();

        this.clearChat = config.isClearChatOnStart();
        this.worldLoaded = config.getWorld().isLoaded();
        this.chatMuted = false;
        this.pvpEnabled = false;
        this.damage = false;
        this.finalHealHappened = false;
        this.firstShrunk = false;
        this.canShrink = true;
        this.whitelist = config.getWhiteList().isEnabledWhitelist();
        this.whitelistPlayers = config.getWhiteList().getPlayers();

        this.zoneId = ZoneId.of(config.getTimeZone());
    }

    @Override
    public List<UUID> getAlivePlayers() {
        return this.players.entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public List<UUID> getSpectators() {
        return Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getUniqueId)
                .filter(uniqueId -> !this.players.getOrDefault(uniqueId, false))
                .toList();
    }

    @Override
    public String getHostName() {
        if (this.host == null) {
            return "NONE";
        } else {
            return this.host.getName();
        }
    }

    @Override
    public void setWhitelist(CommandSender sender, boolean enabled) {
        this.whitelist = enabled;

        String player = sender instanceof Player ? sender.getName() : "Console";
        if (enabled) {
            Utilities.broadcast(Message.WHITELIST_ON.toString()
                    .replace("<player>", player));
        } else {
            Utilities.broadcast(Message.WHITELIST_OFF.toString()
                    .replace("<player>", player));
        }
    }
}
