package me.lotiny.misty.bukkit.provider.menus.user;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.gui.Gui;
import io.fairyproject.bukkit.gui.pane.NormalPane;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.api.profile.stats.StatType;
import me.lotiny.misty.api.profile.stats.Stats;
import me.lotiny.misty.bukkit.provider.menus.MenuItem;
import me.lotiny.misty.bukkit.provider.menus.MistyMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Map;

@RequiredArgsConstructor
public class StatsMenu extends MistyMenu {

    private final Profile profile;

    @Override
    public Component getTitle(Player player) {
        return Component.text(profile.getName() + "'s Stats");
    }

    @Override
    public int getRows(Player player) {
        return 3;
    }

    @Override
    public Map<Integer, MenuItem> getButtons(Player player, NormalPane pane, Gui gui) {
        return Map.of(13, MenuItem.of(
                ItemBuilder.of(XMaterial.GOLDEN_APPLE)
                        .name("&6&lUHC")
                        .lore(
                                " ",
                                "&bGame Played&7: &f" + profile.getStats(StatType.GAME_PLAYED).getAmount(),
                                " ",
                                "&bELO&7: &f" + profile.getStats(StatType.ELO).getAmount(),
                                "&bWins&7: &f" + profile.getStats(StatType.WINS).getAmount(),
                                " ",
                                "&bKills&7: &f" + profile.getStats(StatType.KILLS).getAmount(),
                                "&bDeaths&7: &f" + profile.getStats(StatType.DEATHS).getAmount(),
                                "&bK/D&7: &f" + getKillDeathRatio(),
                                " ",
                                "&bDiamond&7: &f" + profile.getStats(StatType.DIAMOND_MINED).getAmount()
                        )
                        .build()
        ));
    }

    private String getKillDeathRatio() {
        Stats kills = profile.getStats(StatType.KILLS);
        Stats deaths = profile.getStats(StatType.DEATHS);
        if (deaths.getAmount() == 0) {
            return kills.getAmount() + ".0";
        }

        double kd = (double) kills.getAmount() / deaths.getAmount();
        return String.format("%.1f", kd);
    }
}
