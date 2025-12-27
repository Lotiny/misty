package me.lotiny.misty.bukkit.command.impl;

import io.fairyproject.bukkit.command.event.BukkitCommandContext;
import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.command.annotation.Arg;
import io.fairyproject.command.annotation.Command;
import io.fairyproject.container.InjectableComponent;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.api.profile.stats.StatType;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.api.team.TeamManager;
import me.lotiny.misty.bukkit.command.AbstractCommand;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.storage.StorageRegistry;
import me.lotiny.misty.bukkit.utils.KeyEx;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.Snapshot;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;

@InjectableComponent
@RequiredArgsConstructor
@Command(value = {"respawn", "revive"}, permissionNode = "misty.command.respawn")
public class RespawnCommand extends AbstractCommand {

    private final GameManager gameManager;
    private final TeamManager teamManager;
    private final StorageRegistry storageRegistry;

    @Command("#")
    public void onRespawn(BukkitCommandContext context, @Arg("target") Player target) {
        mustBePlayer(context, player ->
                handleRespawn(player, target)
        );
    }

    private void handleRespawn(Player player, Player target) {
        GameRegistry registry = gameManager.getRegistry();
        if (registry.getState() != GameState.INGAME) {
            player.sendMessage(Message.WRONG_STATE.toString());
            return;
        }

        UUID targetUuid = target.getUniqueId();
        Profile targetProfile = storageRegistry.getProfile(targetUuid);
        Snapshot snapshot = Metadata.provideForPlayer(targetUuid).getOrNull(KeyEx.SNAPSHOT_KEY);
        if (snapshot == null || !UHCUtils.isAlive(targetUuid)) {
            player.sendMessage(Message.RESPAWN_CANT.toString());
            return;
        }

        Team team = snapshot.getTeam();
        int teamSize = gameManager.getGame().getSetting().getTeamSize();
        if (teamSize > 1 && team.getMembers(true).size() >= teamSize) {
            player.sendMessage(Message.RESPAWN_CANT.toString());
            return;
        }

        teamManager.getTeams().putIfAbsent(team.getId(), team);

        target.setGameMode(GameMode.SURVIVAL);
        if (!UHCUtils.isAlive(targetUuid)) {
            registry.getPlayers().replace(targetUuid, true);
        }
        targetProfile.getStats(StatType.DEATHS).decrease();

        int elo = snapshot.getElo();
        if (elo != 0) {
            targetProfile.getStats(StatType.ELO).increase(elo);
        }

        PlayerUtils.setMaxHealth(target, snapshot.getMaxHealth());
        PlayerUtils.healPlayer(target);
        target.teleport(snapshot.getLocation());
        target.getInventory().setContents(snapshot.getItems());
        target.getInventory().setArmorContents(snapshot.getArmors());
        target.setLevel(snapshot.getLevel());

        player.sendMessage(Message.RESPAWN_DONE.toString()
                .replace("<player>", target.getName()));
    }
}
