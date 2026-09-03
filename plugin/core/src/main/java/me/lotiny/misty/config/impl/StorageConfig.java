package me.lotiny.misty.config.impl;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import me.lotiny.misty.config.BaseConfig;
import me.lotiny.misty.storage.StorageType;

@Getter
@Configuration
public final class StorageConfig extends BaseConfig {

    @Comment({"Choose storage type you want to use. (MONGODB, MYSQL, MARIADB, POSTGRES and H2)"})
    private StorageType storageType = StorageType.H2;

    @Comment("Setting up the connection for MongoDB.")
    private MongoDB mongoDb = new MongoDB();

    @Comment("Setting up the connection for SQL Type.")
    private SQL sql = new SQL();

    @Getter
    @Configuration
    public static class MongoDB {

        private String connection = "mongodb://localhost:27017";
        private String database = "misty";
    }

    @Getter
    @Configuration
    public static class SQL {

        private String host = "localhost";
        private int port = 3306;
        private String database = "misty";
        private String username = "root";
        private String password = "password";
        private boolean useSsl = false;
        private int maximumPoolSize = 10;
    }
}
