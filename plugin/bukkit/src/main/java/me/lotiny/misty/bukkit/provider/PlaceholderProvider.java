package me.lotiny.misty.bukkit.provider;

import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.bukkit.util.LegacyAdventureUtil;
import io.fairyproject.container.Autowired;
import io.fairyproject.mc.MCPlayer;
import io.fairyproject.util.FormatUtil;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameSetting;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.task.AbstractScheduleTask;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.impl.ScoreboardConfig;
import me.lotiny.misty.bukkit.hook.PluginHookManager;
import me.lotiny.misty.bukkit.hook.impl.chunk.ChunkLoader;
import me.lotiny.misty.bukkit.manager.border.BorderManager;
import me.lotiny.misty.bukkit.scenario.impl.NoCleanScenario;
import me.lotiny.misty.bukkit.task.BorderTask;
import me.lotiny.misty.bukkit.utils.KeyEx;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import me.lotiny.misty.bukkit.utils.cooldown.CombatCooldown;
import me.lotiny.misty.bukkit.utils.cooldown.PlayerCooldown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.TagPattern;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class PlaceholderProvider {

    @Autowired
    private static GameManager gameManager;
    @Autowired
    private static ScenarioManager scenarioManager;
    @Autowired
    private static BorderManager borderManager;
    @Autowired
    private static PluginHookManager pluginHookManager;

    public TagResolver tagResolver(MCPlayer mcPlayer) {
        if (mcPlayer == null) {
            return TagResolver.empty();
        }

        TagResolver.Builder tagResolver = TagResolver.builder();
        GameRegistry registry = gameManager.getRegistry();

        tagResolver.resolver(parsed("players", registry.getAlivePlayers().size()));
        tagResolver.resolver(parsed("total_players", registry.getPlayers().size()));
        tagResolver.resolver(parsed("host", registry.getHostName()));
        tagResolver.resolver(parsed("border", gameManager.getGame().getSetting().getBorderSize()));
        tagResolver.resolver(parsed("game_type", UHCUtils.getGameType()));
        tagResolver.resolver(parsed("shrink_in", getShrinkIn()));
        tagResolver.resolver(parsed("game_timer", getTaskTimer(registry.getGameTask())));
        tagResolver.resolver(parsed("reboot_timer", getTaskTimer(registry.getRebootTask())));

        Player player = mcPlayer.as(Player.class);
        tagResolver.resolver(parsed("kills", UHCUtils.getGameKills(player)));

        Team team = UHCUtils.getTeam(player);
        if (team != null) {
            tagResolver.resolver(parsed("team_kills", team.getTeamKills()));
        }

        ScoreboardConfig.Placeholder placeholder = Config.getScoreboardConfig().getScoreboard().getPlaceholder();
        addStartTimerTag(tagResolver, placeholder, registry);
        addNoCleanTag(tagResolver, placeholder, player);
        addDoNotDisturbTag(tagResolver, placeholder, player);
        addChunkLoaderTag(tagResolver);
        addScatterTag(tagResolver, registry);

        return tagResolver.build();
    }

    public Component decode(MCPlayer mcPlayer, String message, TagResolver tagResolver) {
        return LegacyAdventureUtil.decode(pluginHookManager.replace(mcPlayer.as(Player.class), message), tagResolver);
    }

    private TagResolver parsed(@TagPattern @NotNull String key, Object value) {
        return Placeholder.parsed(key, String.valueOf(value));
    }

    private void addStartTimerTag(TagResolver.Builder tagResolver, ScoreboardConfig.Placeholder placeholder, GameRegistry registry) {
        tagResolver.resolver(parsed("start_timer", placeholder.getStartTime()
                .replace("<time>", getTaskTimer(registry.getStartTask()))));
    }

    private void addNoCleanTag(TagResolver.Builder tagResolver, ScoreboardConfig.Placeholder placeholder, Player player) {
        if (scenarioManager.isEnabled("No Clean")) {
            PlayerCooldown cooldown = Metadata.provideForPlayer(player.getUniqueId()).getOrNull(NoCleanScenario.NO_CLEAN_KEY);
            if (cooldown != null) {
                String replace = cooldown.getCooldownMillis(player);
                tagResolver.resolver(parsed("no_clean", placeholder.getNoClean()
                        .replace("<time>", replace)));
            }
        }
    }

    private void addDoNotDisturbTag(TagResolver.Builder tagResolver, ScoreboardConfig.Placeholder placeholder, Player player) {
        if (scenarioManager.isEnabled("Do Not Disturb")) {
            Team team = UHCUtils.getTeam(player);
            if (team == null) return;

            CombatCooldown cooldown = team.getStorage().getOrNull(KeyEx.COMBAT_COOLDOWN_KEY);
            if (cooldown == null) return;

            Team linked = cooldown.getTeam();
            String time = cooldown.getCooldownMillis(team);

            tagResolver.resolver(parsed("dnd", placeholder.getDoNotDisturb()
                    .replace("<linked>", String.valueOf(linked.getId()))
                    .replace("<time>", time)));
        }
    }

    private void addChunkLoaderTag(TagResolver.Builder tagResolver) {
        ChunkLoader chunkLoader = pluginHookManager.getChunkLoader();
        if (!chunkLoader.isCompleted()) {
            tagResolver.resolver(parsed("world", chunkLoader.getWorld()));
            tagResolver.resolver(parsed("chunks", chunkLoader.getChunks()));
            tagResolver.resolver(parsed("progress", chunkLoader.getProgress()));
        }
    }

    private void addScatterTag(TagResolver.Builder tagResolver, GameRegistry registry) {
        int scatterPercent = Math.round((float) registry.getPlayersScattered().size() / registry.getPlayers().size() * 100);
        tagResolver.resolver(parsed("scatter_percentage", scatterPercent + "%"));
    }

    private String getTaskTimer(AbstractScheduleTask task) {
        if (task == null) {
            return "0:00";
        }

        return FormatUtil.formatSeconds(task.getSeconds());
    }

    private String getShrinkIn() {
        GameSetting setting = gameManager.getGame().getSetting();
        if (setting.getBorderSize() == borderManager.getAllowedBorderSizes()[0]) {
            return "Last";
        }

        BorderTask borderTask = (BorderTask) gameManager.getRegistry().getBorderTask();
        if (borderTask == null) {
            return "0s";
        }

        int time = borderTask.getSeconds();
        int firstShrinkTime = setting.getFirstShrink() * 60;
        int remainingTime;

        if (gameManager.getRegistry().isFirstShrunk()) {
            remainingTime = time;
        } else {
            remainingTime = firstShrinkTime - time;
        }

        if (remainingTime < 60) {
            return remainingTime + "s";
        } else {
            return ((remainingTime / 60) + 1) + "m";
        }
    }
}
