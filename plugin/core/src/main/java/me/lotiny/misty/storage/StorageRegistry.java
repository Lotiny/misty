package me.lotiny.misty.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.fairyproject.bootstrap.bukkit.BukkitPlugin;
import io.fairyproject.container.DependsOn;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.container.PreDestroy;
import io.fairyproject.log.Log;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.config.Config;
import me.lotiny.misty.config.ConfigManager;
import me.lotiny.misty.config.impl.StorageConfig;
import me.lotiny.misty.manager.leaderboard.LeaderboardHologram;
import me.lotiny.misty.manager.leaderboard.LeaderboardHologramSerializer;
import me.lotiny.misty.profile.ProfileSerializer;
import me.lotiny.misty.storage.impl.MongoStorage;
import me.lotiny.misty.storage.impl.sql.H2Storage;
import me.lotiny.misty.storage.impl.sql.MySqlStorage;
import me.lotiny.misty.storage.impl.sql.PostgresqlStorage;
import me.lotiny.misty.utils.Utilities;
import org.bson.Document;

import java.io.File;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@DependsOn(ConfigManager.class)
@InjectableComponent
@RequiredArgsConstructor
public class StorageRegistry {

    public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Getter
    private MongoDatabase mongoDatabase;

    private MongoClient mongoClient;

    @Getter
    private HikariDataSource dataSource;

    @Getter
    private Storage<Profile> profileStorage;

    @Getter
    private Storage<LeaderboardHologram> leaderboardHologramStorage;

    @PostInitialize
    public void onPostInit() {
        StorageType storageType = Config.getStorageConfig().getStorageType();

        boolean connected =
                switch (storageType) {
                    case MONGODB -> connectMongoDB();
                    case MYSQL -> connectSql("MySQL", createMySqlConfig());
                    case MARIADB -> connectSql("MariaDB", createMariaDbConfig());
                    case POSTGRES -> connectSql("PostgreSQL", createPostgresqlConfig());
                    case H2 -> connectSql("H2", createH2Config());
                };

        if (!connected) {
            Utilities.disable();
            return;
        }

        profileStorage = createStorage(storageType, "uniqueId", "player", new ProfileSerializer());
        profileStorage.init();

        leaderboardHologramStorage =
                createStorage(storageType, "leaderboardType", "holograms", new LeaderboardHologramSerializer());
        leaderboardHologramStorage.init();
        leaderboardHologramStorage.loadAll();
    }

    @PreDestroy
    public void onPreDestroy() {
        if (profileStorage != null) {
            profileStorage.saveAll();
        }
        if (leaderboardHologramStorage != null) {
            leaderboardHologramStorage.saveAll();
        }

        try {
            if (mongoClient != null) {
                mongoClient.close();
            }
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
            }
        } catch (Exception ignore) {
        }
    }

    public Profile getProfile(UUID uuid) {
        return profileStorage.get(uuid.toString());
    }

    private <T> Storage<T> createStorage(
            StorageType storageType, String uniqueId, String collection, StorageSerializer<T> serializer) {
        return switch (storageType) {
            case MONGODB -> new MongoStorage<>(this, uniqueId, collection, serializer);
            case POSTGRES -> new PostgresqlStorage<>(this, uniqueId, collection, serializer);
            case H2 -> new H2Storage<>(this, uniqueId, collection, serializer);
            case MYSQL, MARIADB -> new MySqlStorage<>(this, uniqueId, collection, serializer);
        };
    }

    public boolean connectMongoDB() {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        StorageConfig.MongoDB mongoDB = Config.getStorageConfig().getMongoDb();
        ServerApi serverApi = ServerApi.builder().version(ServerApiVersion.V1).build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoDB.getConnection()))
                .serverApi(serverApi)
                .build();

        try {
            this.mongoClient = MongoClients.create(settings);
            this.mongoDatabase = this.mongoClient.getDatabase(mongoDB.getDatabase());
            this.mongoDatabase.runCommand(new Document("ping", 1));
            return true;
        } catch (MongoException e) {
            Log.error("Failed to connect to MongoDB, disabling the plugin...", e);
            return false;
        }
    }

    private boolean connectSql(String dialectName, HikariConfig config) {
        Logger.getLogger("com.zaxxer.hikari").setLevel(Level.WARNING);
        try {
            this.dataSource = new HikariDataSource(config);
            Log.info("Successfully connected to " + dialectName + " and setup connection pool.");
            return true;
        } catch (Exception e) {
            Log.error("Failed to connect to " + dialectName + ", disabling the plugin...", e);
            return false;
        }
    }

    private HikariConfig createBaseRemoteConfig(String driver, String url) {
        StorageConfig.SQL sql = Config.getStorageConfig().getSql();
        HikariConfig config = new HikariConfig();

        config.setDriverClassName(driver);
        config.setJdbcUrl(url);
        config.setUsername(sql.getUsername());
        config.setPassword(sql.getPassword());
        config.setMaximumPoolSize(sql.getMaximumPoolSize());

        return config;
    }

    private HikariConfig createMySqlConfig() {
        StorageConfig.SQL sql = Config.getStorageConfig().getSql();
        String url = "jdbc:mysql://" + sql.getHost() + ":" + sql.getPort() + "/" + sql.getDatabase() + "?useSSL="
                + sql.isUseSsl();

        HikariConfig config = createBaseRemoteConfig("com.mysql.cj.jdbc.Driver", url);
        applyMySqlOptimizations(config);
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        return config;
    }

    private HikariConfig createMariaDbConfig() {
        StorageConfig.SQL sql = Config.getStorageConfig().getSql();
        String url = "jdbc:mariadb://" + sql.getHost() + ":" + sql.getPort() + "/" + sql.getDatabase() + "?useSsl="
                + sql.isUseSsl();

        HikariConfig config = createBaseRemoteConfig("org.mariadb.jdbc.Driver", url);
        applyMySqlOptimizations(config);
        return config;
    }

    private void applyMySqlOptimizations(HikariConfig config) {
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
    }

    private HikariConfig createPostgresqlConfig() {
        StorageConfig.SQL sql = Config.getStorageConfig().getSql();
        String sslMode = sql.isUseSsl() ? "require" : "disable";
        String url = "jdbc:postgresql://" + sql.getHost() + ":" + sql.getPort() + "/" + sql.getDatabase() + "?sslmode="
                + sslMode;

        HikariConfig config = createBaseRemoteConfig("org.postgresql.Driver", url);
        config.addDataSourceProperty("reWriteBatchedInserts", "true");
        return config;
    }

    private HikariConfig createH2Config() {
        File dataFolder = new File(BukkitPlugin.INSTANCE.getDataFolder(), "database");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File dbFile = new File(dataFolder, "data");
        String url = "jdbc:h2:" + dbFile.getAbsolutePath() + ";MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE";

        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl(url);
        config.setMaximumPoolSize(10);
        return config;
    }
}
