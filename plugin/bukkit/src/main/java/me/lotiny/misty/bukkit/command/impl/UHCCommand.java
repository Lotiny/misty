package me.lotiny.misty.bukkit.command.impl;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.bukkit.util.BukkitPos;
import io.fairyproject.command.CommandContext;
import io.fairyproject.command.MessageType;
import io.fairyproject.command.annotation.Arg;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.command.annotation.CommandPresence;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.mc.util.Position;
import io.fairyproject.util.CC;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.customitem.CustomItem;
import me.lotiny.misty.api.customitem.CustomItemRegistry;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.command.MistyPresenceProvider;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.impl.MainConfig;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.provider.menus.staff.SettingsMenu;
import me.lotiny.misty.bukkit.storage.StorageRegistry;
import me.lotiny.misty.bukkit.utils.TeamEx;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import java.util.UUID;

@InjectableComponent
@RequiredArgsConstructor
@Command(value = {"uhc", "game"}, permissionNode = "misty.command.uhc")
@CommandPresence(MistyPresenceProvider.class)
public class UHCCommand extends AbstractCommand {

    private final GameManager gameManager;
    private final ScenarioManager scenarioManager;
    private final StorageRegistry storageRegistry;
    private final CustomItemRegistry customItemRegistry;

    @Override
    public void onHelp(CommandContext context) {
        context.sendMessage(
                MessageType.INFO,
                CC.CHAT_BAR,
                "&b/uhc host <player> &7- &fSet or remove game's host",
                "&b/uhc settings &7- &fOpen main uhc settings",
                "&b/uhc setspawn &7- &fSet main lobby spawn",
                "&b/uhc checkbp <player> &7- &fOpen player's team backpack",
                "&b/uhc clearlag &7- &fKill all animals and monster",
                "&b/uhc customitem <item> &7- &fGive yourself a customcraft",
                "&b/uhc forcepvp &7- &fForce pvp to be enabled",
                "&b/uhc forcefinalheal &7- &fForce final heal to be executed",
                CC.CHAT_BAR
        );
    }

    @Command("settings")
    public void onSetting(BukkitCommandContext context) {
        mustBePlayer(context, player -> new SettingsMenu().open(player));
    }

    @Command({"setspawn", "setlobby"})
    public void onSetSpawn(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            Position position = BukkitPos.toMCPos(player.getLocation());
            MainConfig config = Config.getMainConfig();
            config.setLobbyLocation(BukkitPos.toBukkitLocation(position));
            config.save();
            player.sendMessage(Message.SET_SPAWN.toString());
        });
    }

    @Command("clearlag")
    public void onClearLag(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            World world = player.getWorld();

            for (Entity entity : world.getEntities()) {
                if (entity instanceof Monster || entity instanceof Animals) {
                    entity.remove();
                }
            }

            player.sendMessage(Message.CLEAR_LAG.toString()
                    .replace("<world>", world.getName()));
        });
    }

    @Command("checkbp")
    public void onCheckBp(BukkitCommandContext context, @Arg("target") Player target) {
        mustBePlayer(context, player -> {
            if (gameManager.getGame().getSetting().getTeamSize() == 1) {
                player.sendMessage(Message.TEAM_DISABLED.toString());
                return;
            }

            Team team = UHCUtils.getTeam(target);
            if (team == null) {
                player.sendMessage(Message.TEAM_NOT_FOUND.toString());
                return;
            }

            player.openInventory(team.getStorage().getOrThrow(TeamEx.TEAM_INVENTORY));
        });
    }

    @Command("host")
    public void onHost(BukkitCommandContext context, @Arg("target") Player target) {
        mustBePlayer(context, player -> {
            GameRegistry registry = gameManager.getRegistry();
            UUID uuid = target.getUniqueId();

            if (registry.getHost() == null) {
                registry.setHost(storageRegistry.getProfile(uuid));
                player.sendMessage(Message.HOST_SET_HOST.toString()
                        .replace("<target>", target.getName()));
            } else {
                if (registry.getHost().getUniqueId().equals(uuid)) {
                    registry.setHost(null);
                    player.sendMessage(Message.HOST_REMOVE_HOST.toString()
                            .replace("<target>", target.getName()));
                } else {
                    player.sendMessage(Message.HOST_ALREADY_HAVE_HOST.toString()
                            .replace("<host>", registry.getHost().getName()));
                }
            }
        });
    }

    @Command("customitem")
    public void onCustomItem(BukkitCommandContext context, @Arg("item") CustomItem customItem) {
        mustBePlayer(context, player -> {
            if (gameManager.getRegistry().getState() != GameState.INGAME) {
                player.sendMessage(Message.WRONG_STATE.toString());
                return;
            }

            if (!scenarioManager.isEnabled("Custom Craft")) {
                player.sendMessage(Message.SCENARIO_NOT_ENABLED.toString()
                        .replace("<scenario>", "Custom Craft"));
                return;
            }

            player.getInventory().addItem(customItemRegistry.createResult(customItem));
            player.sendMessage(CC.translate("&aYou received x1 " + customItem.getName() + "!"));
        });
    }

    @Command("forcepvp")
    public void onForcePvp(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            if (gameManager.getRegistry().isPvpEnabled()) {
                player.sendMessage(CC.translate("&cPvP is already enabled!"));
                return;
            }

            gameManager.endGracePeriod();
        });
    }

    @Command("forcefinalheal")
    public void onForceFinalHeal(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            if (gameManager.getRegistry().isFinalHealHappened()) {
                player.sendMessage(CC.translate("&cFinal Heal is already happened!"));
                return;
            }

            gameManager.executeFinalHeal();
        });
    }
}
