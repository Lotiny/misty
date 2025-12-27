package me.lotiny.misty.bukkit.utils;

import io.fairyproject.util.FastRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RandomSelector<T> {

    private final List<T> list;
    private final FastRandom random;

    public RandomSelector(Set<T> set, FastRandom random) {
        this.list = new ArrayList<>(set);
        this.random = random;
    }

    public T getRandomElement() {
        return list.get(random.nextInt(list.size()));
    }
}
