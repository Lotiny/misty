package me.lotiny.misty.bukkit.provider;

import io.fairyproject.bukkit.util.LegacyAdventureUtil;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.mc.MCPlayer;
import io.fairyproject.sidebar.SidebarLine;
import io.fairyproject.sidebar.SidebarProvider;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.impl.ScoreboardConfig;
import me.lotiny.misty.bukkit.hook.PluginHookManager;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@InjectableComponent
@RequiredArgsConstructor
public class ScoreboardProvider extends PlaceholderProvider implements SidebarProvider {

    private final GameManager gameManager;
    private final ScenarioManager scenarioManager;
    private final PluginHookManager pluginHookManager;

    @Override
    public @Nullable Component getTitle(@NotNull MCPlayer mcPlayer) {
        return LegacyAdventureUtil.decode(Config.getScoreboardConfig().getScoreboard().getTitle());
    }

    @Override
    public @Nullable List<SidebarLine> getLines(@NotNull MCPlayer mcPlayer) {
        List<SidebarLine> sidebarLines = new ArrayList<>();
        List<String> lines = getScoreboardLines();

        TagResolver resolver = tagResolver(mcPlayer);
        Player player = mcPlayer.as(Player.class);
        Team team = UHCUtils.getTeam(player);

        for (String line : lines) {
            if (line.equals("<start_timer>") && gameManager.getRegistry().getStartTask() == null)
                continue;

            if (line.equals("<no_clean>") && (!scenarioManager.isEnabled("No Clean") || !UHCUtils.hasNoClean(player)))
                continue;

            if (line.equals("<dnd>") && (!scenarioManager.isEnabled("Do Not Disturb") || team == null || !UHCUtils.isInCombat(team)))
                continue;

            if (line.equals("<scenarios>")) {
                List<Scenario> enabledScenarioImpls = scenarioManager.getEnabledScenarios(gameManager);
                if (enabledScenarioImpls.isEmpty()) {
                    sidebarLines.add(SidebarLine.of(decode(mcPlayer, "<white>- <red>None", resolver)));
                } else {
                    int maxLines = 2;
                    int more = Math.max(0, enabledScenarioImpls.size() - maxLines);

                    for (int i = 0; i < Math.min(maxLines, enabledScenarioImpls.size()); i++) {
                        String scenarioLine = "<white>- <aqua>" + enabledScenarioImpls.get(i).getName();
                        sidebarLines.add(SidebarLine.of(decode(mcPlayer, scenarioLine, resolver)));
                    }

                    if (more > 0) {
                        sidebarLines.add(SidebarLine.of(decode(mcPlayer, "<white>- " + more + " more...", resolver)));
                    }
                }
                continue;
            }

            sidebarLines.add(SidebarLine.of(decode(mcPlayer, line, resolver)));
        }

        return sidebarLines;
    }

    private List<String> getScoreboardLines() {
        ScoreboardConfig config = Config.getScoreboardConfig();
        List<String> lines = new ArrayList<>();
        if (!pluginHookManager.getChunkLoader().isCompleted()) {
            lines = config.getScoreboard().getLoadChunk();
        } else {
            switch (gameManager.getRegistry().getState()) {
                case LOBBY -> lines = config.getScoreboard().getLobby();
                case SCATTERING -> lines = config.getScoreboard().getScatter();
                case ENDING -> lines = config.getScoreboard().getEnd();
                case INGAME -> {
                    if (gameManager.getGame().getSetting().getTeamSize() > 1) {
                        lines = config.getScoreboard().getGameTeam();
                    } else {
                        lines = config.getScoreboard().getGameFFA();
                    }
                }
            }
        }
        return lines;
    }
}
