package me.lotiny.misty.hook.impl.viaversion;

import io.fairyproject.log.Log;
import me.lotiny.misty.hook.PluginHook;

public class ViaVersionHook implements PluginHook {

    @Override
    public void register() {
        Log.info("Hooked 'ViaVersion' for Legacy Version support.");
    }
}
