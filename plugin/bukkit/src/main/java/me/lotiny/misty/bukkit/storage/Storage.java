package me.lotiny.misty.bukkit.storage;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Storage<T> {

    void init();

    T get(String key);

    CompletableFuture<T> getAsync(String key);

    Optional<T> find(String key, String value);

    CompletableFuture<Optional<T>> findAsync(String key, String value);

    Collection<T> getAll();

    void create(String key);

    void load(T object);

    void loadAll();

    Map<Integer, T> getTops(int count, String key);

    boolean delete(T object);

    void deleteAll();

    void save(T object);

    void saveAsync(T object);

    void saveAll();
}
