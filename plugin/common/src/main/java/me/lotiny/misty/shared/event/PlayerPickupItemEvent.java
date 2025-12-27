package me.lotiny.misty.shared.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public class PlayerPickupItemEvent extends Event implements Cancellable {

    @Getter
    private static final HandlerList handlerList = new HandlerList();
    private final Player player;
    private final Item item;
    private final int remaining;
    private final Event bukkitPickupItemEvent;
    @Setter
    private boolean cancelled;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
