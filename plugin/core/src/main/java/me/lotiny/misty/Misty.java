package me.lotiny.misty;

import io.fairyproject.Fairy;
import io.fairyproject.FairyLaunch;
import io.fairyproject.bukkit.util.SpigotUtil;
import io.fairyproject.library.Library;
import io.fairyproject.log.Log;
import io.fairyproject.plugin.Plugin;
import me.lotiny.misty.utils.Utilities;
import org.jetbrains.annotations.Nullable;

@FairyLaunch
public class Misty extends Plugin {

    @Override
    public void onPreEnable() {
        try {
            loadLibrary("org.mongodb:mongodb-driver-sync:5.6.0");
            loadLibrary("org.mongodb:mongodb-driver-core:5.6.0");
            loadLibrary("org.mongodb:bson:5.6.0");
            loadLibrary("com.zaxxer:HikariCP:7.0.2");
            loadLibrary("de.maxhenkel.voicechat:voicechat-api:2.5.36", "https://maven.maxhenkel.de/repository/public");
        } catch (Exception e) {
            Log.error("Failed to load required libraries. Disabling plugin.", e);
            Utilities.disable();
        }
    }

    @Override
    public void onPluginEnable() {
        SpigotUtil.init();
    }

    private void loadLibrary(String gradle) {
        loadLibrary(gradle, null);
    }

    private void loadLibrary(String gradle, @Nullable String repository) {
        Library.Builder builder = Library.builder().gradle(gradle);
        if (repository != null) {
            builder.repository(repository);
        }

        Fairy.getLibraryHandler().loadLibrary(builder.build(), true);

        Log.info("[Dependency] Successfully loaded library: " + gradle);
    }
}
