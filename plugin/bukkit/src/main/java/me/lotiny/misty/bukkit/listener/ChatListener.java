package me.lotiny.misty.bukkit.listener;

import io.fairyproject.bukkit.listener.RegisterAsListener;
import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.metadata.MetadataMap;
import io.fairyproject.util.CC;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameSetting;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.bukkit.Permission;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.impl.MainConfig;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.hook.PluginHookManager;
import me.lotiny.misty.bukkit.hook.rank.IRank;
import me.lotiny.misty.bukkit.hook.rank.RankManager;
import me.lotiny.misty.bukkit.utils.KeyEx;
import me.lotiny.misty.bukkit.utils.TeamEx;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Objects;
import java.util.UUID;

@InjectableComponent
@RequiredArgsConstructor
@RegisterAsListener
public class ChatListener implements Listener {

    private final GameManager gameManager;
    private final ScenarioManager scenarioManager;
    private final RankManager rankManager;
    private final PluginHookManager pluginHookManager;

    private String teamPrefix;
    private String hostPrefix;
    private String modPrefix;

    @PostInitialize
    public void onPostInit() {
        MainConfig config = Config.getMainConfig();
        teamPrefix = CC.translate(config.getChatPrefix().getTeamPrefix());
        hostPrefix = CC.translate(config.getChatPrefix().getHostPrefix());
        modPrefix = CC.translate(config.getChatPrefix().getModPrefix());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        GameRegistry registry = gameManager.getRegistry();
        MetadataMap meta = Metadata.provideForPlayer(player);

        if (handleTeamChat(event, meta)) return;
        if (handleMutedChat(event, player, registry)) return;

        handleGlobalChat(event, player, uuid, registry, meta);
    }

    private boolean handleTeamChat(AsyncPlayerChatEvent event, MetadataMap meta) {
        boolean isTeamChat = meta.getOrDefault(TeamEx.TEAM_CHAT, false);
        if (!isTeamChat) return false;

        Team team = meta.getOrNull(KeyEx.TEAM_KEY);
        if (team != null && (gameManager.getGame().getSetting().getTeamSize() != 1
                || scenarioManager.isEnabled("Love At First Sight") || scenarioManager.isEnabled("Red vs Blue"))) {

            if (Config.getMainConfig().getChatFormat().isEnabled()) {
                setChatFormat(event, Config.getMainConfig().getChatFormat().getTeam());
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
        if (!registry.isChatMuted()) return false;

        if (!player.hasPermission(Permission.HOST_PERMISSION)) {
            event.setCancelled(true);
            player.sendMessage(Message.CHAT_DISABLED.toString());
            return true;
        }
        return false;
    }

    private void handleGlobalChat(AsyncPlayerChatEvent event, Player player, UUID uuid, GameRegistry registry, MetadataMap meta) {
        boolean isSpectator = !player.hasPermission(Permission.HOST_PERMISSION) && !UHCUtils.isAlive(uuid);
        MainConfig config = Config.getMainConfig();
        if (config.getChatFormat().isEnabled()) {
            setChatFormat(event, isSpectator ? config.getChatFormat().getSpectator() : config.getChatFormat().getNormal());
        }

        if (isSpectator) {
            event.getRecipients().clear();
            registry.getSpectators().stream()
                    .map(Bukkit::getPlayer)
                    .filter(Objects::nonNull)
                    .forEach(event.getRecipients()::add);
        }
    }

    private void setChatFormat(AsyncPlayerChatEvent event, String format) {
        if (format == null) return;

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

        String chatFormat = format
                .replace("<prefix>", prefix + rankPrefix)
                .replace("<player>", rankColor + player.getName())
                .replace("<suffix>", rankSuffix)
                .replace("<message>", event.getMessage());

        event.setFormat(pluginHookManager.replace(player, chatFormat));
    }
}
