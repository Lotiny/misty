package me.lotiny.misty.bukkit.config.impl;

import com.cryptomorin.xseries.XMaterial;
import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.lotiny.misty.bukkit.Permission;
import me.lotiny.misty.bukkit.config.BaseConfig;

import java.util.List;
import java.util.Map;

@Getter
@Configuration
public final class HotBarConfig extends BaseConfig {

    private Map<Integer, HotBarItem> lobby = Map.of(
            0, new HotBarItem(
                    null,
                    XMaterial.BOOK,
                    "&bGame Configuration",
                    List.of(
                            " ",
                            "&7Right-click to see current",
                            "&7game configuration.",
                            " "
                    ),
                    "cmd:config"
            ),
            1, new HotBarItem(
                    Permission.HOST_PERMISSION,
                    XMaterial.REDSTONE_TORCH,
                    "&bGame Settings",
                    List.of(
                            " ",
                            "&7Right-click to settings",
                            "&7server configuration.",
                            " "
                    ),
                    "cmd:uhc settings"
            ),
            4, new HotBarItem(
                    null,
                    XMaterial.NAME_TAG,
                    "&bStats",
                    List.of(
                            " ",
                            "&7Right-click to see",
                            "&7your uhc stats.",
                            " "
                    ),
                    "cmd:stats"
            ),
            7, new HotBarItem(
                    null,
                    XMaterial.EMERALD,
                    "&bLeaderboard",
                    List.of(
                            " ",
                            "&7Right-click to see",
                            "&7server leaderboards.",
                            " "
                    ),
                    "cmd:leaderboards"
            ),
            8, new HotBarItem(
                    null,
                    XMaterial.DIAMOND_SWORD,
                    "&bPractice",
                    List.of(
                            " ",
                            "&7Right-click to join",
                            "&7and practice your skills",
                            "&7in practice arena.",
                            " "
                    ),
                    "cmd:practice"
            )
    );

    private Map<Integer, HotBarItem> spectator = Map.of(
            0, new HotBarItem(
                    null,
                    XMaterial.COMPASS,
                    "&dSpectator Compass",
                    List.of(
                            " ",
                            "&7Right-click to see",
                            "&7all players in game.",
                            " "
                    ),
                    "act:alive-players"
            ),
            1, new HotBarItem(
                    Permission.HOST_PERMISSION,
                    XMaterial.QUARTZ,
                    "&eTeleport To Random Player",
                    List.of(
                            " ",
                            "&7Right-click to teleport",
                            "&7to random player in-game.",
                            " "
                    ),
                    "act:teleport-random-player"
            ),
            4, new HotBarItem(
                    null,
                    XMaterial.WHITE_CARPET,
                    "&fBetter View",
                    List.of(
                            " ",
                            "&7Holding this to get better-view.",
                            " "
                    ),
                    null
            ),
            5, new HotBarItem(
                    null,
                    XMaterial.EMERALD,
                    "&aTeleport To Center",
                    List.of(
                            " ",
                            "&7Right-click to teleport",
                            "&7to the center of uhc world.",
                            " "
                    ),
                    "act:teleport-center"
            ),
            8, new HotBarItem(
                    Permission.HOST_PERMISSION,
                    XMaterial.BOOK,
                    "&bInspect Player Inventory",
                    List.of(
                            " ",
                            "&7Right-click player to",
                            "&7inspect inventory.",
                            " "
                    ),
                    "act:inspect-inventory"
            )
    );

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Configuration
    public static class HotBarItem {

        @Comment("Choose the permission for this item. Only the player with this permission will get this item.")
        private String permission;

        @Comment("Material type for this item.")
        private XMaterial material;

        @Comment("Display name for this item.")
        private String name;

        @Comment("Lore for this item.")
        private List<String> lore;

        @Comment("The function when right-click on this item. (cmd:<command>, act:<action>)")
        private String onClick;
    }
}
