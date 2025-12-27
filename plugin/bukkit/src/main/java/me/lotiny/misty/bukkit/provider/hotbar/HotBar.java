package me.lotiny.misty.bukkit.provider.hotbar;

import io.fairyproject.Fairy;
import io.fairyproject.bukkit.util.items.FairyItem;
import io.fairyproject.bukkit.util.items.FairyItemRegistry;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.bukkit.util.items.behaviour.ItemBehaviour;
import io.fairyproject.container.Autowired;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.data.MetaKey;
import io.fairyproject.util.CC;
import io.fairyproject.util.FastRandom;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.impl.HotBarConfig;
import me.lotiny.misty.bukkit.provider.hotbar.listener.HotBarListenerRegistry;
import me.lotiny.misty.bukkit.provider.menus.staff.InspectMenu;
import me.lotiny.misty.bukkit.provider.menus.user.AlivePlayersMenu;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

@InjectableComponent
@RequiredArgsConstructor
public class HotBar {

    @Autowired
    private static HotBar instance;

    private final FairyItemRegistry itemRegistry;
    private final HotBarListenerRegistry listenerRegistry;
    private final GameManager gameManager;

    private Map<String, Integer> lobbyItems;
    private Map<String, Integer> spectatorItems;

    private MetaKey<String> permissionKey;

    public static HotBar get() {
        return instance;
    }

    @PostInitialize
    public void onPostInit() {
        lobbyItems = new ConcurrentHashMap<>();
        spectatorItems = new ConcurrentHashMap<>();
        permissionKey = MetaKey.createString("misty:item-permission");

        createItem();
    }

    public void createItem() {
        BiConsumer<Map<String, Integer>, Map<Integer, HotBarConfig.HotBarItem>> createItems = (itemsMap, itemsConfig) ->
                itemsConfig.forEach((slot, item) -> {
                    String onClick = item.getOnClick();
                    String[] parts = null;

                    if (onClick != null && !onClick.isEmpty()) {
                        parts = onClick.split(":", 2);
                    }

                    String id = UUID.randomUUID().toString();
                    FairyItem.Builder builder = FairyItem.builder(id)
                            .item(ItemBuilder.of(item.getMaterial())
                                    .name(item.getName())
                                    .lore(item.getLore())
                                    .itemFlag(ItemFlag.HIDE_ATTRIBUTES)
                            );

                    if (parts != null && parts.length == 2) {
                        String type = parts[0].toLowerCase();
                        String value = parts[1];

                        builder.behaviour(ItemBehaviour.interact(listenerRegistry, (player, itemStack, action, event) -> {
                            switch (type) {
                                case "cmd", "command":
                                    player.performCommand(value);
                                    break;
                                case "act", "action":
                                    switch (value.toLowerCase()) {
                                        case "alive-players":
                                            new AlivePlayersMenu().open(player);
                                            break;
                                        case "teleport-center":
                                            player.teleport(UHCUtils.getCenter());
                                            break;
                                        case "teleport-random-player":
                                            Player randomPlayer = getRandomPlayer();
                                            if (randomPlayer != null) {
                                                player.teleport(randomPlayer);
                                            } else {
                                                player.sendMessage(CC.RED + "Teleporting to a random player has failed...");
                                            }
                                            break;
                                    }
                                    break;
                            }
                        }, Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK));

                        builder.behaviour(ItemBehaviour.ofEvent(PlayerInteractEntityEvent.class, (event, behaviour) -> {
                            Player player = event.getPlayer();
                            ItemStack itemStack = PlayerUtils.getItemInHand(player);

                            if (behaviour.matches(player, itemStack)) {
                                if (type.equals("action") && value.equalsIgnoreCase("inspect-inventory")) {
                                    Entity entity = event.getRightClicked();
                                    if (!(entity instanceof Player clickedPlayer)) return;

                                    new InspectMenu(clickedPlayer).open(player);
                                }
                            }
                        }));
                    }

                    FairyItem fairyItem = builder.create(itemRegistry);

                    String itemPermission = item.getPermission();
                    if (itemPermission != null && !itemPermission.isEmpty()) {
                        fairyItem.getMetaStorage().put(permissionKey, itemPermission);
                    }

                    itemsMap.put(id, slot);
                });

        HotBarConfig config = Config.getHotBarConfig();
        createItems.accept(lobbyItems, config.getLobby());
        createItems.accept(spectatorItems, config.getSpectator());
    }

    public void apply(Player player) {
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();

        Map<String, Integer> itemsToApply = !UHCUtils.isAlive(player.getUniqueId()) ? spectatorItems : lobbyItems;
        itemsToApply.forEach((id, slot) -> {
            FairyItem item = itemRegistry.get(id);
            String permission = item.getMetaStorage().getOrNull(permissionKey);
            if (permission != null && !player.hasPermission(permission)) return;

            ItemStack itemStack = item.provideItemStack(player);
            player.getInventory().setItem(slot, itemStack);
        });
    }

    private Player getRandomPlayer() {
        List<UUID> players = gameManager.getRegistry().getAlivePlayers();

        if (players.isEmpty()) {
            return null;
        }

        FastRandom random = Fairy.random();
        int index;
        Player toReturnPlayer;

        do {
            index = random.nextInt(players.size());
            toReturnPlayer = Bukkit.getPlayer(players.get(index));
        } while (toReturnPlayer == null);

        return toReturnPlayer;
    }
}
