package me.lotiny.misty.bukkit.command.impl;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.bukkit.command.util.CommandUtil;
import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.bukkit.util.Players;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.mc.scheduler.MCSchedulers;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.event.PlayerScatterEvent;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.api.team.TeamManager;
import me.lotiny.misty.bukkit.Permission;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.utils.KeyEx;
import me.lotiny.misty.bukkit.utils.Snapshot;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@InjectableComponent
@RequiredArgsConstructor
@Command(value = "rescatter", permissionNode = "misty.command.rescatter")
public class RescatterCommand extends AbstractCommand {

    private final GameManager gameManager;
    private final TeamManager teamManager;

    private final Map<UUID, Integer> usages = new HashMap<>();

    @Command("#")
    public void onCommand(BukkitCommandContext context) {
        mustBePlayer(context, player -> {
            GameRegistry registry = gameManager.getRegistry();
            UUID uuid = player.getUniqueId();
            Snapshot snapshot = Metadata.provideForPlayer(uuid).getOrNull(KeyEx.SNAPSHOT_KEY);

            if (UHCUtils.isAlive(uuid) || registry.isPvpEnabled() || snapshot == null) {
                player.sendMessage(Message.RE_SCATTER_CANT.toString());
                return;
            }

            Team team = snapshot.getTeam();
            int teamSize = gameManager.getGame().getSetting().getTeamSize();

            if (teamSize > 1 && team.getMembers(true).size() >= teamSize) {
                player.sendMessage(Message.RE_SCATTER_CANT.toString());
                return;
            }

            int limit = this.getLimit(player);
            int usage = usages.getOrDefault(uuid, 0);
            if (usage >= limit) {
                player.sendMessage(Message.RE_SCATTER_LIMITED.toString()
                        .replace("<amount>", String.valueOf(limit)));
                return;
            }

            teamManager.getTeams().putIfAbsent(team.getId(), team);

            player.setGameMode(GameMode.SURVIVAL);
            if (!UHCUtils.isAlive(uuid)) {
                registry.getPlayers().replace(uuid, true);
            }
            Metadata.provideForPlayer(uuid).remove(KeyEx.SNAPSHOT_KEY);

            usages.put(uuid, usage + 1);
            gameManager.teleportToRandomLocation(player, gameManager.getGame().getSetting().getBorderSize());
            Players.clear(player);
            UHCUtils.giveStarterItem(player);

            MCSchedulers.getGlobalScheduler().schedule(() -> Bukkit.getPluginManager().callEvent(new PlayerScatterEvent(player, true)));

            player.sendMessage(Message.RE_SCATTER_DONE.toString()
                    .replace("<amount>", String.valueOf(limit - usage - 1)));
        });
    }

    private int getLimit(Player player) {
        if (player.hasPermission(Permission.RESCATTER_USAGE + "*")) {
            return Integer.MAX_VALUE;
        }

        int usage = 1;
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            String perm = info.getPermission();

            if (perm.startsWith(Permission.RESCATTER_USAGE)) {
                String suffix = perm.substring((Permission.RESCATTER_USAGE).length());
                if (CommandUtil.isInteger(suffix)) {
                    usage = Integer.parseInt(suffix);
                    break;
                }
            }
        }

        return usage;
    }
}
