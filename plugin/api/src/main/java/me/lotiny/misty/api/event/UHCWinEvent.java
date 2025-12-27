package me.lotiny.misty.api.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class UHCWinEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final List<UUID> winners;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
