package me.lotiny.misty.api.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class UHCMinuteEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final int minutes;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
