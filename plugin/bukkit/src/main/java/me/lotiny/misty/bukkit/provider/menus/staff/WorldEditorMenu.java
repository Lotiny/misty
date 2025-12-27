package me.lotiny.misty.bukkit.provider.menus.staff;

import com.cryptomorin.xseries.XMaterial;
import io.fairyproject.bukkit.gui.Gui;
import io.fairyproject.bukkit.gui.pane.NormalPane;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import io.fairyproject.util.CC;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.bukkit.manager.WorldManager;
import me.lotiny.misty.bukkit.provider.menus.MenuItem;
import me.lotiny.misty.bukkit.provider.menus.MistyMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class WorldEditorMenu extends MistyMenu {

    @Autowired
    private static GameManager gameManager;
    @Autowired
    private static WorldManager worldManager;

    @Override
    public Component getTitle(Player player) {
        return Component.text("World Editor");
    }

    @Override
    public int getRows(Player player) {
        return 3;
    }

    @Override
    public Map<Integer, MenuItem> getButtons(Player player, NormalPane pane, Gui gui) {
        Map<Integer, MenuItem> buttons = new HashMap<>();

        buttons.put(2, MenuItem.of(
                ItemBuilder.of(XMaterial.GRASS_BLOCK)
                        .name("&2&lCreate World")
                        .build(),
                (clickedPlayer, clickType) -> {
                    clickedPlayer.closeInventory();

                    if (Bukkit.getWorld(gameManager.getRegistry().getUhcWorld()) == null) {
                        worldManager.createWorld(gameManager.getRegistry().getUhcWorld(), World.Environment.NORMAL, WorldType.NORMAL);
                    } else {
                        clickedPlayer.sendMessage(CC.RED + "The UHC world does already exist!");
                    }
                }
        ));

        buttons.put(4, MenuItem.of(
                ItemBuilder.of(XMaterial.GRASS_BLOCK)
                        .name("&2&lLoad World")
                        .build(),
                (clickedPlayer, clickType) -> {
                    clickedPlayer.closeInventory();

                    if (Bukkit.getWorld(gameManager.getRegistry().getUhcWorld()) != null) {
                        worldManager.loadWorld(gameManager.getRegistry().getUhcWorld(), gameManager.getGame().getSetting().getBorderSize());
                    } else {
                        clickedPlayer.sendMessage(CC.RED + "The UHC world does not exist!");
                    }
                }
        ));

        buttons.put(6, MenuItem.of(
                ItemBuilder.of(XMaterial.GRASS_BLOCK)
                        .name("&2&lDelete World")
                        .build(),
                (clickedPlayer, clickType) -> {
                    clickedPlayer.closeInventory();

                    World world = Bukkit.getWorld(gameManager.getRegistry().getUhcWorld());
                    if (world != null) {
                        if (world.getPlayers().isEmpty()) {
                            worldManager.deleteWorld(gameManager.getRegistry().getUhcWorld());
                        } else {
                            clickedPlayer.sendMessage(CC.RED + "Cannot delete world because there are players on it!");
                        }
                    } else {
                        clickedPlayer.sendMessage(CC.RED + "The UHC world does not exist!");
                    }
                }
        ));

        buttons.put(13, MenuItem.of(
                ItemBuilder.of(XMaterial.BEACON)
                        .name("&bRegenerate World")
                        .build(),
                (clickedPlayer, clickType) -> {
                    clickedPlayer.closeInventory();

                    GameRegistry registry = gameManager.getRegistry();
                    World gameWorld = Bukkit.getWorld(registry.getUhcWorld());
                    World netherWorld = Bukkit.getWorld(registry.getNetherWorld());

                    if (gameWorld != null) {
                        worldManager.unloadWorld(gameWorld);
                        worldManager.deleteWorld(gameWorld.getName());
                    }

                    if (netherWorld != null) {
                        worldManager.unloadWorld(netherWorld);
                        worldManager.deleteWorld(netherWorld.getName());
                    }

                    worldManager.createWorld(registry.getUhcWorld(), World.Environment.NORMAL, WorldType.NORMAL);
                    worldManager.createWorld(registry.getNetherWorld(), World.Environment.NETHER, WorldType.NORMAL);

                    clickedPlayer.sendMessage(CC.GREEN + "Successfully regenerate game world and nether world!");

                    worldManager.loadWorld(registry.getUhcWorld(), gameManager.getGame().getSetting().getBorderSize());
                }
        ));

        buttons.put(20, MenuItem.of(
                ItemBuilder.of(XMaterial.NETHERRACK)
                        .name("&4&lCreate Nether")
                        .build(),
                (clickedPlayer, clickType) -> {
                    clickedPlayer.closeInventory();

                    if (Bukkit.getWorld(gameManager.getRegistry().getNetherWorld()) == null) {
                        worldManager.createWorld(gameManager.getRegistry().getNetherWorld(), World.Environment.NETHER, WorldType.NORMAL);
                    } else {
                        clickedPlayer.sendMessage(CC.RED + "The UHC nether does already exist!");
                    }
                }
        ));

        buttons.put(22, MenuItem.of(
                ItemBuilder.of(XMaterial.NETHERRACK)
                        .name("&4&lLoad Nether")
                        .build(),
                (clickedPlayer, clickType) -> {
                    clickedPlayer.closeInventory();

                    if (Bukkit.getWorld(gameManager.getRegistry().getNetherWorld()) != null) {
                        worldManager.loadWorld(gameManager.getRegistry().getNetherWorld(), gameManager.getGame().getSetting().getBorderSize() / worldManager.getNetherScale());
                    } else {
                        clickedPlayer.sendMessage(CC.RED + "The UHC nether does not exist!");
                    }
                }
        ));

        buttons.put(24, MenuItem.of(
                ItemBuilder.of(XMaterial.NETHERRACK)
                        .name("&4&lDelete Nether")
                        .build(),
                (clickedPlayer, clickType) -> {
                    clickedPlayer.closeInventory();

                    World world = Bukkit.getWorld(gameManager.getRegistry().getNetherWorld());
                    if (world != null) {
                        if (world.getPlayers().isEmpty()) {
                            worldManager.deleteWorld(gameManager.getRegistry().getNetherWorld());
                        } else {
                            clickedPlayer.sendMessage(CC.RED + "Cannot delete nether because there are players on it!");
                        }
                    } else {
                        clickedPlayer.sendMessage(CC.RED + "The UHC nether does not exist!");
                    }
                }
        ));

        return buttons;
    }
}
