package me.lotiny.misty.bukkit.command.parameters;

import io.fairyproject.bukkit.command.parameters.BukkitArgTransformer;
import io.fairyproject.container.InjectableComponent;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.bukkit.storage.StorageRegistry;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

@InjectableComponent
@RequiredArgsConstructor
public class ProfileArgTransformer extends BukkitArgTransformer<Profile> {

    private final StorageRegistry storageRegistry;

    @Override
    public Class[] type() {
        return new Class[]{Profile.class};
    }

    @Override
    public Profile transform(CommandSender sender, String source) {
        Player player = (sender instanceof Player && source.equals("self"))
                ? (Player) sender
                : Bukkit.getPlayer(source);

        if (player != null) {
            return storageRegistry.getProfile(player.getUniqueId());
        }

        Optional<Profile> profileOpt = storageRegistry.getProfileStorage()
                .findAsync("name", source)
                .join();

        return profileOpt.orElseGet(() -> this.fail("No player with the name " + source + " found."));
    }

    @Override
    public List<String> tabComplete(Player player, String source) {
        return Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .toList();
    }
}
