package me.lotiny.misty.bukkit.provider.menus.staff.configuration;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import io.fairyproject.bukkit.gui.Gui;
import io.fairyproject.bukkit.gui.pane.NormalPane;
import io.fairyproject.bukkit.gui.pane.PaginatedPane;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.ConfigType;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameSetting;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.bukkit.Permission;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.provider.menus.MenuItem;
import me.lotiny.misty.bukkit.provider.menus.MistyPaginatedMenu;
import me.lotiny.misty.bukkit.provider.menus.staff.BorderSizeMenu;
import me.lotiny.misty.bukkit.provider.menus.user.ScenariosMenu;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import me.lotiny.misty.bukkit.utils.Utilities;
import me.lotiny.misty.bukkit.utils.VersionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public class ConfigMenu extends MistyPaginatedMenu {

    private static final String VALUE_SEPARATOR = "&7: &f";
    private static final int SLOT_HOST = 4;
    private static final int SLOT_SCENARIOS = 40;
    @Autowired
    private static GameManager gameManager;
    private final GameSetting setting;

    @Override
    public Component getTitle(Player player) {
        return Component.text("Configuration");
    }

    @Override
    public int getRows(Player player) {
        return 5;
    }

    @Override
    public List<MenuItem> getButtons(Player player, PaginatedPane pane, Gui gui) {
        List<MenuItem> buttons = new ArrayList<>();

        for (ConfigType configType : ConfigType.values()) {
            if (configType.getMaterial() == XMaterial.POTION) continue;
            switch (configType) {
                case GAME_TYPE -> buttons.add(buildTeamSizeButton());
                case BORDER_SIZE -> buttons.add(buildBorderButton());
                default -> buttons.add(buildConfigButton(configType, configType.get(setting)));
            }
        }

        for (PotionConfig potion : PotionConfig.values()) {
            boolean current = potion.getCurrent(setting);
            buttons.add(MenuItem.of(
                    buildPotionConfigItem(potion, current),
                    (clickedPlayer, clickType) -> {
                        if (!player.hasPermission(Permission.HOST_PERMISSION)) return;
                        setting.setConfig(potion.getConfigType(), !potion.getCurrent(setting), player);
                        playClick(clickedPlayer);
                        open(player);
                    })
            );
        }

        return buttons;
    }

    @Override
    public Map<Integer, MenuItem> getBorderButtons(Player player, NormalPane topPane, NormalPane bottomPane, Gui gui) {
        return Map.of(
                SLOT_HOST, MenuItem.of(
                        ItemBuilder.of(Utilities.createSkull("http://textures.minecraft.net/texture/5a559548dacceefb14ee938de2cd76a3d0c23ce053041bf48780088dda6d4c18"))
                                .name(gameManager.getRegistry().getHost() == null ?
                                        "&eHost &cTBD" :
                                        "&eHost &f" + gameManager.getRegistry().getHost().getName())
                                .lore(new ArrayList<>())
                                .build()
                ),
                SLOT_SCENARIOS, MenuItem.of(
                        ItemBuilder.of(XMaterial.BOOK)
                                .name("&2Scenarios")
                                .build(),
                        (clickedPlayer, clickType) -> {
                            new ScenariosMenu().open(clickedPlayer);
                            playClick(clickedPlayer);
                        }
                )
        );
    }

    private MenuItem buildTeamSizeButton() {
        ConfigType configType = ConfigType.GAME_TYPE;
        return MenuItem.of(
                ItemBuilder.of(configType.getMaterial())
                        .name("&bTeam Size" + VALUE_SEPARATOR + UHCUtils.getGameType())
                        .build(),
                ((player, clickType) -> {
                    if (!player.hasPermission(Permission.HOST_PERMISSION)) return;
                    if (gameManager.getRegistry().getState() != GameState.LOBBY) {
                        player.sendMessage(Message.WRONG_STATE.toString());
                        return;
                    }

                    new ConfigChangeMenu(configType, setting.getTeamSize()).open(player);
                    playClick(player);
                })
        );
    }

    private MenuItem buildBorderButton() {
        return MenuItem.of(
                ItemBuilder.of(XMaterial.BEDROCK)
                        .name("&bBorder Size" + VALUE_SEPARATOR + setting.getBorderSize())
                        .build(),
                (player, clickType) -> {
                    if (!player.hasPermission(Permission.HOST_PERMISSION)) return;
                    new BorderSizeMenu().open(player);
                    playClick(player);
                }
        );
    }

    private MenuItem buildConfigButton(ConfigType configType, Object value) {
        return MenuItem.of(
                ItemBuilder.of(configType.getMaterial())
                        .name("&b" + Utilities.getFormattedName(configType.name()) + VALUE_SEPARATOR + value)
                        .build(),
                ((player, clickType) -> {
                    if (!player.hasPermission(Permission.HOST_PERMISSION)) return;
                    if (gameManager.getRegistry().getState() != GameState.LOBBY) {
                        player.sendMessage(Message.WRONG_STATE.toString());
                        return;
                    }

                    if (configType.getExpectedType() == Integer.class) {
                        new ConfigChangeMenu(configType, (int) value).open(player);
                    } else {
                        setting.setConfig(configType, !(boolean) value, player);
                        open(player);
                    }

                    playClick(player);
                })
        );
    }

    private ItemStack buildPotionConfigItem(PotionConfig potion, boolean config) {
        if (VersionUtils.isHigher(9, 0)) {
            ItemStack item = ItemBuilder.of(XMaterial.POTION)
                    .name("&b" + potion.getName() + VALUE_SEPARATOR + config)
                    .build();

            PotionMeta meta = (PotionMeta) item.getItemMeta();
            if (meta != null) {
                PotionEffect effect = potion.getPotion().buildPotionEffect(20 * 30, 1);
                if (effect != null) {
                    meta.addCustomEffect(effect, true);
                    item.setItemMeta(meta);
                }
            }
            return item;
        }

        return ItemBuilder.of(XMaterial.POTION)
                .data(potion.getLegacyData())
                .name("&b" + potion.getName() + VALUE_SEPARATOR + config)
                .build();
    }

    @RequiredArgsConstructor
    private enum PotionConfig {

        SPEED_1(8194, XPotion.SPEED, "Speed 1", ConfigType.SPEED_1, GameSetting::isSpeed1),
        SPEED_2(8226, XPotion.SPEED, "Speed 2", ConfigType.SPEED_2, GameSetting::isSpeed2),
        STRENGTH_1(8201, XPotion.STRENGTH, "Strength 1", ConfigType.STRENGTH_1, GameSetting::isStrength1),
        STRENGTH_2(8233, XPotion.STRENGTH, "Strength 2", ConfigType.STRENGTH_2, GameSetting::isStrength2),
        POISON(8196, XPotion.POISON, "Poison", ConfigType.POISON, GameSetting::isPoison),
        INVISIBLE(8238, XPotion.INVISIBILITY, "Invisible", ConfigType.INVISIBLE, GameSetting::isInvisible);

        @Getter
        private final int legacyData;
        @Getter
        private final XPotion potion;
        @Getter
        private final String name;
        @Getter
        private final ConfigType configType;
        private final Function<GameSetting, Boolean> valueExtractor;

        public boolean getCurrent(GameSetting setting) {
            return valueExtractor.apply(setting);
        }
    }
}
