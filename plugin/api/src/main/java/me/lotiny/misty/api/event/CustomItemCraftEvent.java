package me.lotiny.misty.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.lotiny.misty.api.customitem.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@AllArgsConstructor
public class CustomItemCraftEvent extends Event implements Cancellable {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final Player player;
    private CustomItem craft;
    private ItemStack result;
    private boolean cancelled;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
