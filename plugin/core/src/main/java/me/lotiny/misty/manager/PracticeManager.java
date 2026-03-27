package me.lotiny.misty.manager;

import com.cryptomorin.xseries.XSound;
import io.fairyproject.Fairy;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.mc.scheduler.MCSchedulers;
import io.fairyproject.util.CC;
import io.fairyproject.util.FastRandom;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.config.Config;
import me.lotiny.misty.config.impl.PracticeConfig;
import me.lotiny.misty.kit.Kit;
import me.lotiny.misty.provider.hotbar.HotBar;
import me.lotiny.misty.utils.LocationEx;
import me.lotiny.misty.utils.Message;
import me.lotiny.misty.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@InjectableComponent
@RequiredArgsConstructor
public class PracticeManager {

    private final GameManager gameManager;
    private final List<UUID> players = new ArrayList<>();
    private Kit kit;
    private int maxPlayers;
    private int teleportRadius;
    private boolean opened;

    @PostInitialize
    public void onPostInit() {
        PracticeConfig config = Config.getPracticeConfig();
        this.kit = config.getKit();
        this.maxPlayers = config.getMaxPlayers();
        this.teleportRadius = config.getTeleportRadius();
        this.opened = false;

        if (config.isAutoEnable()) {
            setOpened(true, Bukkit.getConsoleSender());
        }
    }

    public void join(Player player) {
        if (!this.opened) {
            player.sendMessage(Message.PRACTICE_IS_DISABLED);
            return;
        }

        if (this.players.size() >= this.maxPlayers) {
            player.sendMessage(Message.PRACTICE_FULL);
            return;
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        this.players.add(player.getUniqueId());

        MCSchedulers.getGlobalScheduler()
                .schedule(
                        () -> {
                            player.getInventory().setArmorContents(kit.getArmors());
                            player.getInventory().setContents(kit.getItems());
                            player.teleport(getRandomLocation());
                            player.setFallDistance(0);
                            XSound.BLOCK_NOTE_BLOCK_BASS.play(player);
                        },
                        5L);
    }

    public void leave(Player player) {
        this.players.remove(player.getUniqueId());

        MCSchedulers.getGlobalScheduler()
                .schedule(
                        () -> {
                            HotBar.get().apply(player);
                            LocationEx.LOBBY.teleport(player);
                            XSound.BLOCK_NOTE_BLOCK_BASS.play(player);
                        },
                        5L);
    }

    public void broadcast(String message) {
        this.players.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(CC.translate(message));
            }
        });
    }

    public void setOpened(boolean opened, CommandSender sender) {
        boolean currentlyOpened = this.opened;

        if (opened) {
            if (gameManager.getRegistry().getState() != GameState.LOBBY) {
                if (!currentlyOpened) {
                    this.opened = false;
                }

                if (sender != null) {
                    sender.sendMessage(CC.translate("&cPractice can only be enabled while the game is in the lobby."));
                }
                return;
            }

            if (currentlyOpened) {
                return;
            }

            this.opened = true;

            Utilities.broadcast(Message.PRACTICE_ENABLED.replace(
                    "<player>", sender instanceof Player ? sender.getName() : "Console"));
            return;
        }

        if (!currentlyOpened) {
            return;
        }

        this.opened = false;

        for (UUID uuid : this.players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                LocationEx.LOBBY.teleport(player);
                HotBar.get().apply(player);
            }
        }

        this.players.clear();
        Utilities.broadcast(
                Message.PRACTICE_DISABLED.replace("<player>", sender instanceof Player ? sender.getName() : "Console"));
    }

    public Location getRandomLocation() {
        FastRandom random = Fairy.random();
        Location location = LocationEx.PRACTICE.getLocation();
        int x = location.getBlockX() + random.nextInt(this.teleportRadius * 2 + 1) - this.teleportRadius;
        int z = location.getBlockZ() + random.nextInt(this.teleportRadius * 2 + 1) - this.teleportRadius;

        World world = location.getWorld();
        if (world == null) {
            throw new IllegalStateException("Practice world is null");
        }

        return new Location(location.getWorld(), x, world.getHighestBlockYAt(x, z), z);
    }

    public boolean isInPractice(Player player) {
        return this.players.contains(player.getUniqueId());
    }
}
