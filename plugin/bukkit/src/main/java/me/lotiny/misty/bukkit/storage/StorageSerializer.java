package me.lotiny.misty.bukkit.storage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.fairyproject.util.ConditionUtils;

public interface StorageSerializer<T> {

    default JsonElement get(JsonObject json, String key) {
        JsonElement element = json.get(key);
        ConditionUtils.notNull(element, "Could not find key '" + key + "' in json: " + json);
        return element;
    }

    String getKey(T object);

    T fromJson(JsonObject json);

    JsonObject toJson(T object);

    T createDefault(String key);
}
