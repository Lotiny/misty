package me.lotiny.misty.bukkit.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.fairyproject.container.DependsOn;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.container.PreDestroy;
import io.fairyproject.log.Log;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.ConfigManager;
import me.lotiny.misty.bukkit.config.impl.StorageConfig;
import me.lotiny.misty.bukkit.manager.leaderboard.LeaderboardHologram;
import me.lotiny.misty.bukkit.manager.leaderboard.LeaderboardHologramSerializer;
import me.lotiny.misty.bukkit.profile.ProfileSerializer;
import me.lotiny.misty.bukkit.storage.impl.MongoStorage;
import me.lotiny.misty.bukkit.storage.impl.MySqlStorage;
import me.lotiny.misty.bukkit.utils.Utilities;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

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
        if (storageType == StorageType.MONGODB) {
            connectMongoDB();
        } else {
            connectMySQL();
        }

        profileStorage = createStorage(storageType, "uniqueId", "player", new ProfileSerializer());
        profileStorage.init();

        leaderboardHologramStorage = createStorage(storageType, "leaderboardType", "holograms", new LeaderboardHologramSerializer());
        leaderboardHologramStorage.init();
        leaderboardHologramStorage.loadAll();
    }

    @PreDestroy
    public void onPreDestroy() {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        scoreboardManager.getMainScoreboard().getTeams().forEach(Team::unregister);

        profileStorage.saveAll();
        leaderboardHologramStorage.saveAll();

        try {
            mongoClient.close();
            dataSource.close();
        } catch (Exception ignore) {
        }
    }

    public Profile getProfile(UUID uuid) {
        return profileStorage.get(uuid.toString());
    }

    private <T> Storage<T> createStorage(StorageType storageType, String uniqueId, String collection, StorageSerializer<T> serializer) {
        if (storageType == StorageType.MONGODB) {
            return new MongoStorage<>(this, uniqueId, collection, serializer);
        } else {
            return new MySqlStorage<>(this, uniqueId, collection, serializer);
        }
    }

    public void connectMongoDB() {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        StorageConfig.MongoDB mongoDB = Config.getStorageConfig().getMongoDb();
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoDB.getConnection()))
                .serverApi(serverApi)
                .build();

        try {
            this.mongoClient = MongoClients.create(settings);
            this.mongoDatabase = this.mongoClient.getDatabase(mongoDB.getDatabase());
            this.mongoDatabase.runCommand(new Document("ping", 1));
        } catch (MongoException e) {
            Log.error("Failed to connect to MongoDB, disabling the plugin...", e);
            Utilities.disable();
        }
    }

    public void connectMySQL() {
        Logger.getLogger("com.zaxxer.hikari").setLevel(Level.WARNING);
        StorageConfig.MySQL mySql = Config.getStorageConfig().getMySql();

        try {
            HikariConfig hikariConfig = new HikariConfig();

            String jdbcUrl = "jdbc:mysql://" + mySql.getHost() + ":" + mySql.getPort() +
                    "/" + mySql.getDatabase() + "?useSSL=" + mySql.isUseSsl();

            hikariConfig.setJdbcUrl(jdbcUrl);
            hikariConfig.setUsername(mySql.getUsername());
            hikariConfig.setPassword(mySql.getPassword());
            hikariConfig.setMaximumPoolSize(mySql.getMaximumPoolSize());

            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
            hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
            hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
            hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
            hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");

            this.dataSource = new HikariDataSource(hikariConfig);
            Log.info("Successfully connected to MySQL and setup connection pool.");

        } catch (Exception e) {
            Log.error("Failed to connect to MySQL, disabling the plugin...", e);
            Utilities.disable();
        }
    }
}
