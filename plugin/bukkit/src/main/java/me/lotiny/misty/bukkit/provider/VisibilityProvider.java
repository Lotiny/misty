package me.lotiny.misty.bukkit.provider;

import io.fairyproject.bukkit.visibility.VisibilityAdapter;
import io.fairyproject.bukkit.visibility.VisibilityOption;
import io.fairyproject.container.InjectableComponent;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.entity.Player;

@InjectableComponent
@RequiredArgsConstructor
public class VisibilityProvider implements VisibilityAdapter {

    @Override
    public VisibilityOption check(Player player, Player target) {
        if (!UHCUtils.isAlive(target.getUniqueId())) {
            return !UHCUtils.isAlive(player.getUniqueId()) ? VisibilityOption.SHOW : VisibilityOption.HIDE;
        }

        return VisibilityOption.SHOW;
    }
}
