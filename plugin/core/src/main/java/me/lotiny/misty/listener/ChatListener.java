package me.lotiny.misty.listener;

import io.fairyproject.bukkit.events.BukkitEventFilter;
import io.fairyproject.bukkit.events.BukkitEventNode;
import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.container.PreDestroy;
import io.fairyproject.event.EventNode;
import io.fairyproject.metadata.MetadataMap;
import io.fairyproject.util.CC;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameSetting;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.config.Config;
import me.lotiny.misty.config.impl.MainConfig;
import me.lotiny.misty.hook.PluginHookManager;
import me.lotiny.misty.hook.rank.IRank;
import me.lotiny.misty.hook.rank.RankManager;
import me.lotiny.misty.utils.KeyEx;
import me.lotiny.misty.utils.Message;
import me.lotiny.misty.utils.Permission;
import me.lotiny.misty.utils.TeamEx;
import me.lotiny.misty.utils.UHCUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

@InjectableComponent
@RequiredArgsConstructor
public class ChatListener {

    private final GameManager gameManager;
    private final RankManager rankManager;
    private final PluginHookManager pluginHookManager;
    private final BukkitEventNode globalNode;

    private String teamPrefix;
    private String hostPrefix;
    private String modPrefix;

    private boolean chatFormat;

    private String normalFormat;
    private String teamFormat;
    private String spectatorFormat;

    private EventNode<PlayerEvent> eventNode;

    @PostInitialize
    public void onPostInit() {
        MainConfig config = Config.getMainConfig();
        this.teamPrefix = CC.translate(config.getChatPrefix().getTeamPrefix());
        this.hostPrefix = CC.translate(config.getChatPrefix().getHostPrefix());
        this.modPrefix = CC.translate(config.getChatPrefix().getModPrefix());

        this.chatFormat = config.getChatFormat().isEnabled();

        this.normalFormat = config.getChatFormat().getNormal();
        this.teamFormat = config.getChatFormat().getTeam();
        this.spectatorFormat = config.getChatFormat().getSpectator();

        this.eventNode = EventNode.type("chat-listeners", BukkitEventFilter.PLAYER);

        eventNode.addListener(AsyncPlayerChatEvent.class, event -> {
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            GameRegistry registry = gameManager.getRegistry();
            MetadataMap meta = Metadata.provideForPlayer(player);

            if (handleTeamChat(event, meta)) return;
            if (handleMutedChat(event, player, registry)) return;

            handleGlobalChat(event, player, uuid, registry);
        });

        globalNode.addChild(eventNode);
    }

    @PreDestroy
    public void onPreDestroy() {
        globalNode.removeChild(eventNode);
    }

    private boolean handleTeamChat(AsyncPlayerChatEvent event, MetadataMap meta) {
        boolean isTeamChat = meta.getOrDefault(TeamEx.TEAM_CHAT, false);
        if (!isTeamChat) {
            return false;
        }

        Team team = meta.getOrNull(KeyEx.TEAM_KEY);
        if (team != null && gameManager.getGame().getSetting().getTeamSize() != 1) {
            if (chatFormat) {
                setChatFormat(event, teamFormat);
            }

            event.getRecipients().clear();
            team.getMembers(false).stream()
                    .map(Bukkit::getPlayer)
                    .filter(Objects::nonNull)
                    .forEach(event.getRecipients()::add);

            return true;
        }

        meta.put(TeamEx.TEAM_CHAT, false);
        return false;
    }

    private boolean handleMutedChat(AsyncPlayerChatEvent event, Player player, GameRegistry registry) {
        if (!registry.isChatMuted()) {
            return false;
        }

        if (!player.hasPermission(Permission.HOST_PERMISSION)) {
            event.setCancelled(true);
            player.sendMessage(Message.CHAT_DISABLED);
            return true;
        }

        return false;
    }

    private void handleGlobalChat(AsyncPlayerChatEvent event, Player player, UUID uuid, GameRegistry registry) {
        boolean isSpectator = !player.hasPermission(Permission.HOST_PERMISSION) && !UHCUtils.isAlive(uuid);
        if (chatFormat) {
            setChatFormat(event, isSpectator ? spectatorFormat : normalFormat);
        }

        if (isSpectator) {
            event.getRecipients().clear();
            registry.getSpectators().stream()
                    .map(Bukkit::getPlayer)
                    .filter(Objects::nonNull)
                    .forEach(event.getRecipients()::add);
        }
    }

    private void setChatFormat(AsyncPlayerChatEvent event, @NotNull String format) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        IRank rank = rankManager.getRank();

        String prefix = "";
        GameSetting setting = gameManager.getGame().getSetting();
        if (setting.getTeamSize() > 1) {
            Team team = UHCUtils.getTeam(player);
            if (team != null) {
                prefix = teamPrefix.replace("<id>", String.valueOf(team.getId()));
            }
        } else if (!UHCUtils.isAlive(uuid)) {
            GameRegistry registry = gameManager.getRegistry();
            if (registry.getHost() != null && registry.getHost().getUniqueId().equals(uuid)) {
                prefix = hostPrefix;
            } else if (player.hasPermission(Permission.HOST_PERMISSION)) {
                prefix = modPrefix;
            }
        }

        String rankPrefix = rank.getRankPrefix(uuid);
        String rankColor = rank.getRankColor(uuid);
        String rankSuffix = rank.getRankSuffix(uuid);

        String chatFormat = format.replace("<prefix>", prefix + rankPrefix)
                .replace("<player>", rankColor + player.getName())
                .replace("<suffix>", rankSuffix)
                .replace("<message>", event.getMessage());

        event.setFormat(pluginHookManager.replace(player, chatFormat));
    }
}
