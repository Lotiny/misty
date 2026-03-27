package me.lotiny.misty.storage.impl;

import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.DeleteResult;
import io.fairyproject.log.Log;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.storage.Storage;
import me.lotiny.misty.storage.StorageRegistry;
import me.lotiny.misty.storage.StorageSerializer;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class MongoStorage<T> implements Storage<T> {

    private final StorageRegistry storageRegistry;
    private final String uniqueKey;
    private final String collectionName;
    private final StorageSerializer<T> serializer;

    private final Map<String, T> cache = new ConcurrentHashMap<>();

    private MongoCollection<Document> collection;

    @Override
    public void init() {
        String name = collectionName.replace("/", "_").replace("-", "_");
        collection = storageRegistry.getMongoDatabase().getCollection(name);
    }

    @Override
    public T get(String key) {
        if (!cache.containsKey(key)) {
            T defaultObject = serializer.createDefault(key);
            load(defaultObject);
        }
        return cache.get(key);
    }

    @Override
    public CompletableFuture<T> getAsync(String key) {
        return CompletableFuture.supplyAsync(() -> get(key));
    }

    @Override
    public Optional<T> find(String key, String value) {
        Document document = collection.find(Filters.eq(key, value)).first();
        if (document != null) {
            JsonObject jsonObject = StorageRegistry.GSON.fromJson(document.toJson(), JsonObject.class);
            T loadedObject = serializer.fromJson(jsonObject);
            return Optional.of(loadedObject);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public CompletableFuture<Optional<T>> findAsync(String key, String value) {
        return CompletableFuture.supplyAsync(() -> find(key, value));
    }

    @Override
    public Collection<T> getAll() {
        return cache.values();
    }

    @Override
    public void create(String key) {
        T defaultObject = serializer.createDefault(key);
        cache.put(key, defaultObject);
    }

    @Override
    public void load(T object) {
        String key = serializer.getKey(object);
        find(uniqueKey, key).ifPresentOrElse(loadedObject -> cache.put(key, loadedObject), () -> create(key));
    }

    @Override
    public void loadAll() {
        for (Document document : collection.find()) {
            try {
                JsonObject jsonObject = StorageRegistry.GSON.fromJson(document.toJson(), JsonObject.class);
                T object = serializer.fromJson(jsonObject);

                if (object != null) {
                    String key = serializer.getKey(object);
                    cache.put(key, object);
                }
            } catch (Exception e) {
                Log.error(e.getMessage());
            }
        }
    }

    @Override
    public Map<Integer, T> getTops(int count, String key) {
        Map<Integer, T> tops = new ConcurrentHashMap<>();

        List<Document> documents = collection
                .find()
                .limit(count)
                .sort(new BasicDBObject("stats." + key, -1))
                .into(new ArrayList<>());
        for (int i = 0; i < documents.size(); i++) {
            int place = i + 1;
            Document document = documents.get(i);
            JsonObject jsonObject = StorageRegistry.GSON.fromJson(document.toJson(), JsonObject.class);
            tops.put(place, serializer.fromJson(jsonObject));
        }

        return tops;
    }

    @Override
    public boolean delete(T object) {
        String key = serializer.getKey(object);
        DeleteResult result = collection.deleteOne(Filters.eq(uniqueKey, key));
        cache.remove(key);
        return result.getDeletedCount() > 0;
    }

    @Override
    public void deleteAll() {
        cache.clear();
        collection.drop();
    }

    @Override
    public void save(T object) {
        String key = serializer.getKey(object);
        JsonObject jsonObject = serializer.toJson(object);
        Document document = Document.parse(StorageRegistry.GSON.toJson(jsonObject));
        collection.replaceOne(Filters.eq(uniqueKey, key), document, new ReplaceOptions().upsert(true));
    }

    @Override
    public void saveAsync(T object) {
        CompletableFuture.runAsync(() -> save(object));
    }

    @Override
    public void saveAll() {
        if (cache.isEmpty()) return;

        List<WriteModel<Document>> operations = new ArrayList<>();

        for (T object : cache.values()) {
            String key = serializer.getKey(object);
            JsonObject jsonObject = serializer.toJson(object);
            Document document = Document.parse(StorageRegistry.GSON.toJson(jsonObject));

            ReplaceOneModel<Document> model =
                    new ReplaceOneModel<>(Filters.eq(uniqueKey, key), document, new ReplaceOptions().upsert(true));

            operations.add(model);
        }

        collection.bulkWrite(operations, new BulkWriteOptions().ordered(false));
    }
}
