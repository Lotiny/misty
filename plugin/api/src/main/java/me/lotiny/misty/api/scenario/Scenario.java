package me.lotiny.misty.api.scenario;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public abstract class Scenario implements Listener {

    @Getter
    @Setter
    private boolean enabled;

    public abstract String getName();

    public abstract ItemStack getIcon();

    public boolean shouldRegister() {
        return true;
    }

    public void onEnable() {

    }

    public void onDisable() {

    }

    public List<ItemStack> getDroppedItems() {
        return Collections.emptyList();
    }

    public boolean equals(Class<? extends Scenario> clazz) {
        return this.getClass().equals(clazz);
    }
}
