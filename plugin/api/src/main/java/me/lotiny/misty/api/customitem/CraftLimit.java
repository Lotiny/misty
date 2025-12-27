package me.lotiny.misty.api.customitem;

import lombok.Getter;

@Getter
public final class CraftLimit {

    private final int amount;

    private CraftLimit(int amount) {
        this.amount = amount;
    }

    /**
     * Creates a limit that allows the item to be crafted the provided number of times.
     *
     * @param amount the allowed crafts
     * @return a new craft limit instance
     */
    public static CraftLimit of(int amount) {
        return new CraftLimit(amount);
    }

    /**
     * Creates a limit that marks the item as unique, meaning it may only be crafted once per player.
     *
     * @return a new unique craft limit instance
     */
    public static CraftLimit unique() {
        return new CraftLimit(-1);
    }

    /**
     * @return {@code true} if the item may only be crafted once per player.
     */
    public boolean isUnique() {
        return this.amount == -1;
    }
}
