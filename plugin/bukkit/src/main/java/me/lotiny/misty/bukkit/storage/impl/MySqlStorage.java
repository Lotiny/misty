package me.lotiny.misty.bukkit.storage.impl;

import com.google.gson.JsonObject;
import io.fairyproject.log.Log;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.bukkit.storage.Storage;
import me.lotiny.misty.bukkit.storage.StorageRegistry;
import me.lotiny.misty.bukkit.storage.StorageSerializer;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@SuppressWarnings({"SqlNoDataSourceInspection", "SqlSourceToSinkFlow"})
public class MySqlStorage<T> implements Storage<T> {

    private static final Pattern SAFE_IDENTIFIER_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");
    private static final Pattern SAFE_JSON_PATH_KEY_PATTERN = Pattern.compile("^[a-zA-Z0-9_.]+$");

    private final StorageRegistry storageRegistry;
    private final String uniqueKey;
    private final String tableName;
    private final StorageSerializer<T> serializer;

    private final Map<String, T> cache = new ConcurrentHashMap<>();

    private final String dataColumn = "data";
    private DataSource dataSource;

    @Override
    public void init() {
        try {
            validateSqlIdentifier(tableName);
            validateSqlIdentifier(uniqueKey);
            validateSqlIdentifier(dataColumn);

            this.dataSource = storageRegistry.getDataSource();

            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                    + uniqueKey + " VARCHAR(255) PRIMARY KEY, "
                    + dataColumn + " JSON"
                    + ");";

            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(sql);
            }
        } catch (SQLException e) {
            Log.error("Failed to initialize storage", e);
        }
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
        try (Connection conn = dataSource.getConnection()) {
            String sql = getSql(key);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, value);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String jsonString = rs.getString(dataColumn);
                        JsonObject jsonObject = StorageRegistry.GSON.fromJson(jsonString, JsonObject.class);
                        T loadedObject = serializer.fromJson(jsonObject);
                        return Optional.of(loadedObject);
                    }
                }
            }
        } catch (SQLException e) {
            Log.error(e.getMessage());
        }
        return Optional.empty();
    }

    private @NotNull String getSql(String key) throws SQLException {
        String sql;
        if (key.equals(uniqueKey)) {
            sql = "SELECT " + dataColumn + " FROM " + tableName + " WHERE " + uniqueKey + " = ?";
        } else {
            validateJsonPathKey(key);

            String jsonPath = "'$." + key + "'";
            sql = "SELECT " + dataColumn + " FROM " + tableName + " WHERE JSON_UNQUOTE(JSON_EXTRACT(" + dataColumn + ", " + jsonPath + ")) = ? LIMIT 1";
        }
        return sql;
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
        find(uniqueKey, key).ifPresentOrElse(
                loadedObject -> cache.put(key, loadedObject),
                () -> create(key)
        );
    }

    @Override
    public void loadAll() {
        String sql = "SELECT " + dataColumn + " FROM " + tableName;

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String jsonString = rs.getString(dataColumn);

                if (jsonString == null) continue;

                JsonObject jsonObject = StorageRegistry.GSON.fromJson(jsonString, JsonObject.class);
                T object = serializer.fromJson(jsonObject);

                if (object != null) {
                    String key = serializer.getKey(object);
                    cache.put(key, object);
                }
            }
        } catch (SQLException e) {
            Log.error("Failed to load all objects from database", e);
        }
    }

    @Override
    public Map<Integer, T> getTops(int count, String key) {
        Map<Integer, T> tops = new ConcurrentHashMap<>();

        try {
            validateSqlIdentifier(key);

            String jsonPath = "'$.stats." + key + "'";
            String sql = "SELECT " + dataColumn + " FROM " + tableName
                    + " ORDER BY CAST(JSON_UNQUOTE(JSON_EXTRACT(" + dataColumn + ", " + jsonPath + ")) AS SIGNED) DESC"
                    + " LIMIT ?";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, count);

                try (ResultSet rs = pstmt.executeQuery()) {
                    int place = 1;
                    while (rs.next()) {
                        String jsonString = rs.getString(dataColumn);
                        JsonObject jsonObject = StorageRegistry.GSON.fromJson(jsonString, JsonObject.class);
                        tops.put(place++, serializer.fromJson(jsonObject));
                    }
                }
            }
        } catch (SQLException e) {
            Log.error(e.getMessage());
        }
        return tops;
    }

    @Override
    public boolean delete(T object) {
        String key = serializer.getKey(object);
        String sql = "DELETE FROM " + tableName + " WHERE " + uniqueKey + " = ?";
        cache.remove(key);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, key);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            Log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public void deleteAll() {
        cache.clear();

        String sql = "TRUNCATE TABLE " + tableName;
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            Log.error(e.getMessage());
        }
    }

    @Override
    public void save(T object) {
        String key = serializer.getKey(object);
        JsonObject jsonObject = serializer.toJson(object);
        String jsonString = StorageRegistry.GSON.toJson(jsonObject);

        String sql = "INSERT INTO " + tableName + " (" + uniqueKey + ", " + dataColumn + ")"
                + " VALUES (?, ?)"
                + " ON DUPLICATE KEY UPDATE " + dataColumn + " = VALUES(" + dataColumn + ")";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, key);
            pstmt.setString(2, jsonString);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            Log.error(e.getMessage());
        }
    }

    @Override
    public void saveAsync(T object) {
        CompletableFuture.runAsync(() -> save(object));
    }

    @Override
    public void saveAll() {
        for (T object : cache.values()) {
            saveAsync(object);
        }
    }

    private void validateSqlIdentifier(String identifier) throws SQLException {
        if (identifier == null || !SAFE_IDENTIFIER_PATTERN.matcher(identifier).matches()) {
            throw new SQLException("Invalid or unsafe SQL identifier detected: " + identifier);
        }
    }

    private void validateJsonPathKey(String key) throws SQLException {
        if (key == null || !SAFE_JSON_PATH_KEY_PATTERN.matcher(key).matches()) {
            throw new SQLException("Invalid or unsafe JSON path key detected: " + key);
        }
    }
}