package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import io.fairyproject.Fairy;
import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import io.fairyproject.metadata.MetadataKey;
import io.fairyproject.util.CC;
import io.fairyproject.util.ConditionUtils;
import io.fairyproject.util.FastRandom;
import me.lotiny.misty.api.event.PlayerScatterEvent;
import me.lotiny.misty.api.event.UHCMinuteEvent;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.bukkit.config.impl.ScenarioConfig;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.scenario.HomeworkUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class HomeworkScenario extends Scenario {

    @Autowired
    private static GameManager gameManager;

    private final MetadataKey<XMaterial> HOMEWORK_ITEM_KEY = MetadataKey.create("misty:homework-item-key", XMaterial.class);
    private final MetadataKey<Integer> HOMEWORK_AMOUNT_KEY = MetadataKey.createIntegerKey("misty:homework-amount-key");

    @Override
    public String getName() {
        return "Homework";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.WRITABLE_BOOK)
                .name("&b" + getName())
                .lore(
                        "&7Every 10 minutes player will get assigned item",
                        "&7you much collect and have it in inventory.",
                        "&7If you do not have the item you will lose 1 full heart.",
                        "&7If you have it you will gain 1 full heart."
                )
                .build();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerScatter(PlayerScatterEvent event) {
        Player player = event.getPlayer();
        handleHomework(player);
    }

    @EventHandler
    public void handleMinute(UHCMinuteEvent event) {
        int minutes = event.getMinutes();

        if (minutes % 10 == 0) {
            for (UUID uuid : gameManager.getRegistry().getAlivePlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    handleHomework(player);
                }
            }
        }
    }

    private void handleHomework(Player player) {
        XMaterial xMaterial = Metadata.provideForPlayer(player).getOrNull(HOMEWORK_ITEM_KEY);
        if (xMaterial != null) {
            ConditionUtils.notNull(xMaterial.get(), "Homework item is null");
            int amount = Metadata.provideForPlayer(player).getOrDefault(HOMEWORK_AMOUNT_KEY, 1);

            if (player.getInventory().containsAtLeast(xMaterial.parseItem(), amount)) {
                PlayerUtils.setMaxHealth(player, PlayerUtils.getMaxHealth(player) + 2);
                player.sendMessage(CC.translate("&e&lYOU DID THE HOMEWORK! &a&lYOU GAIN 1 MORE HEART."));
            } else {
                PlayerUtils.setMaxHealth(player, Math.max(2, PlayerUtils.getMaxHealth(player) - 2));
                player.sendMessage(CC.translate("&e&lYOU DIDN'T DO THE HOMEWORK. &c&lYOU LOSE 1 HEART."));
            }
        }

        FastRandom random = Fairy.random();
        ScenarioConfig.Homework homework = HomeworkUtils.get().getRandomHomework();

        XMaterial material = homework.getMaterial();
        Metadata.provideForPlayer(player).put(HOMEWORK_ITEM_KEY, material);

        int amount = random.nextInt(homework.getMinAmount(), homework.getMaxAmount());
        Metadata.provideForPlayer(player).put(HOMEWORK_AMOUNT_KEY, amount);

        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&6&lThe homework has been assigned!"));
        player.sendMessage(" ");
        player.sendMessage(CC.translate("&7- &a" + amount + "x " + material.friendlyName()));
        player.sendMessage(CC.CHAT_BAR);
        XSound.ENTITY_PLAYER_LEVELUP.play(player);
    }
}
