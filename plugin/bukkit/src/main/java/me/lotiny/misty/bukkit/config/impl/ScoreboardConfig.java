package me.lotiny.misty.bukkit.config.impl;

import de.exlll.configlib.Configuration;
import lombok.Getter;
import me.lotiny.misty.bukkit.config.BaseConfig;

import java.util.List;

@Getter
@Configuration
public final class ScoreboardConfig extends BaseConfig {

    private Scoreboard scoreboard = new Scoreboard();

    @Getter
    @Configuration
    public static class Scoreboard {

        private String title = "<aqua><b>Misty <white><b>UHC";
        private Placeholder placeholder = new Placeholder();
        private List<String> loadChunk = List.of(
                "<gray><st>------------------",
                "<aqua>Chunks Loading...",
                " ",
                "<white>World<gray>: <aqua><world>",
                "<white>Total Chunks<gray>: <aqua><chunks>",
                "<white>Progress<gray>: <aqua><progress>%",
                "<gray><st>------------------"
        );
        private List<String> lobby = List.of(
                "<gray><st>------------------",
                "<white>Players<gray>: <aqua><players>",
                "<white>Type<gray>: <aqua><game_type>",
                " ",
                "<start_timer>",
                "<aqua>Scenarios:",
                "<scenarios>",
                "<gray><st>------------------"
        );
        private List<String> scatter = List.of(
                "<gray><st>------------------",
                "<white>Players<gray>: <aqua><players>",
                "<white>Type<gray>: <aqua><game_type>",
                " ",
                "<white>Scatter<gray>: <aqua><scatter_percentage>",
                "<gray><st>------------------"
        );
        private List<String> gameFFA = List.of(
                "<gray><st>------------------",
                "<white>Game Time<gray>: <aqua><game_timer>",
                "<white>Players<gray>: <aqua><players>/<total_players>",
                "<white>Kills<gray>: <aqua><kills>",
                "<white>Border<gray>: <aqua><border> <gray>(<red><shrink_in><gray>)",
                "<dnd>",
                "<no_clean>",
                "<gray><st>------------------"
        );
        private List<String> gameTeam = List.of(
                "<gray><st>------------------",
                "<white>Game Time<gray>: <aqua><game_timer>",
                "<white>Players<gray>: <aqua><players>/<total_players>",
                "<white>Kills<gray>: <aqua><kills>",
                "<white>Team Kills<gray>: <aqua><team_kills>",
                "<white>Border<gray>: <aqua><border> <gray>(<red><shrink_in><gray>)",
                "<dnd>",
                "<no_clean>",
                "<gray><st>------------------"
        );
        private List<String> end = List.of(
                "<gray><st>------------------",
                "<white>Game Time<gray>: <aqua><game_time>",
                " ",
                "<red>Reboot in <dark_red><b><reboot_time>",
                "<gray><st>------------------"
        );
    }

    @Getter
    @Configuration
    public static class Placeholder {

        private String startTime = "<white>Start in <aqua><time>";
        private String noClean = "<white>No Clean<gray>: <aqua><time>s";
        private String doNotDisturb = "<white>DnD<gray>: <aqua><time>s";
    }
}
