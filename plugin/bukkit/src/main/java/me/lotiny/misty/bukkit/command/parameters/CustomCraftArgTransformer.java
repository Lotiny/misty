package me.lotiny.misty.bukkit.command.parameters;

import io.fairyproject.bukkit.command.parameters.BukkitArgTransformer;
import io.fairyproject.container.InjectableComponent;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.customitem.CustomItem;
import me.lotiny.misty.api.customitem.CustomItemRegistry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@InjectableComponent
@RequiredArgsConstructor
public class CustomCraftArgTransformer extends BukkitArgTransformer<CustomItem> {

    private final CustomItemRegistry customItemRegistry;

    @Override
    public Class[] type() {
        return new Class[]{CustomItem.class};
    }

    @Override
    public CustomItem transform(CommandSender sender, String source) {
        CustomItem customItem = customItemRegistry.getCustomItems().get(source);
        if (customItem == null) {
            return this.fail("No custom-item with the name " + source + " found.");
        }

        return customItem;
    }

    @Override
    public List<String> tabComplete(Player player, String source) {
        return customItemRegistry.getCustomItems().keySet()
                .stream()
                .toList();
    }
}
