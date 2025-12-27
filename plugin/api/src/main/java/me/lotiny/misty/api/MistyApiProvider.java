package me.lotiny.misty.api;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicesManager;

import java.util.Optional;

@UtilityClass
public class MistyApiProvider {

    public MistyApi get() {
        ServicesManager servicesManager = Bukkit.getServicesManager();
        Optional<MistyApi> api = Optional.ofNullable(servicesManager.load(MistyApi.class));
        return api.orElseThrow(() -> new IllegalStateException("Misty API is not registered yet."));
    }
}
