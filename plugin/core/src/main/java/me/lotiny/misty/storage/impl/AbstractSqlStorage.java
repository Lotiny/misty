package me.lotiny.misty.storage.impl;

import com.google.gson.JsonObject;
import io.fairyproject.log.Log;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.storage.Storage;
import me.lotiny.misty.storage.StorageRegistry;
import me.lotiny.misty.storage.StorageSerializer;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import javax.sql.DataSource;

@RequiredArgsConstructor
@SuppressWarnings({"SqlNoDataSourceInspection", "SqlSourceToSinkFlow"})
public abstract class AbstractSqlStorage<T> implements Storage<T> {

    protected static final Pattern SAFE_IDENTIFIER_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");
    protected static final Pattern SAFE_JSON_PATH_KEY_PATTERN = Pattern.compile("^[a-zA-Z0-9_.]+$");

    protected final StorageRegistry storageRegistry;
    protected final String uniqueKey;
    protected final String tableName;
    protected final StorageSerializer<T> serializer;

    protected final Map<String, T> cache = new ConcurrentHashMap<>();
    protected final String dataColumn = "data";
    protected DataSource dataSource;

    protected abstract String getCreateTableSql();

    protected abstract String getFindJsonSql(String key);

    protected abstract String getTopsSql(String key);

    protected abstract String getUpsertSql();

    protected abstract void bindJsonParam(PreparedStatement pstmt, int index, String json) throws SQLException;

    @Override
    public void init() {
        try {
            validateSqlIdentifier(tableName);
            validateSqlIdentifier(uniqueKey);
            validateSqlIdentifier(dataColumn);

            this.dataSource = storageRegistry.getDataSource();

            try (Connection conn = dataSource.getConnection();
                    Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(getCreateTableSql());
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
                        return Optional.ofNullable(serializer.fromJson(jsonObject));
                    }
                }
            }
        } catch (SQLException e) {
            Log.error(e.getMessage());
        }
        return Optional.empty();
    }

    private @NotNull String getSql(String key) throws SQLException {
        if (key.equals(uniqueKey)) {
            return "SELECT " + dataColumn + " FROM " + tableName + " WHERE " + uniqueKey + " = ?";
        }
        validateJsonPathKey(key);
        return getFindJsonSql(key);
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
        find(uniqueKey, key).ifPresentOrElse(loaded -> cache.put(key, loaded), () -> create(key));
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
                    cache.put(serializer.getKey(object), object);
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
            String sql = getTopsSql(key);

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
            return pstmt.executeUpdate() > 0;
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

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(getUpsertSql())) {

            pstmt.setString(1, key);
            bindJsonParam(pstmt, 2, jsonString);
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
        if (cache.isEmpty()) return;

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(getUpsertSql())) {
                for (T object : cache.values()) {
                    String key = serializer.getKey(object);
                    JsonObject jsonObject = serializer.toJson(object);
                    String jsonString = StorageRegistry.GSON.toJson(jsonObject);

                    pstmt.setString(1, key);
                    bindJsonParam(pstmt, 2, jsonString);
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                Log.error("Failed to save batch, rolling back", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            Log.error("Failed to obtain connection for batch save", e);
        }
    }

    protected void validateSqlIdentifier(String identifier) throws SQLException {
        if (identifier == null || !SAFE_IDENTIFIER_PATTERN.matcher(identifier).matches()) {
            throw new SQLException("Invalid or unsafe SQL identifier detected: " + identifier);
        }
    }

    protected void validateJsonPathKey(String key) throws SQLException {
        if (key == null || !SAFE_JSON_PATH_KEY_PATTERN.matcher(key).matches()) {
            throw new SQLException("Invalid or unsafe JSON path key detected: " + key);
        }
    }
}
