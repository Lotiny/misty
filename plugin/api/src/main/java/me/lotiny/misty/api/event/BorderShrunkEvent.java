package me.lotiny.misty.api.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class BorderShrunkEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final int size;
    private final World world;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
