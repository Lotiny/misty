package me.lotiny.misty.bukkit.hook.impl.svc;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import io.fairyproject.bootstrap.bukkit.BukkitPlugin;
import io.fairyproject.log.Log;
import lombok.Getter;
import me.lotiny.misty.bukkit.hook.PluginHook;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SVCHook implements PluginHook, VoicechatPlugin, Listener {

    private final Map<Integer, Group> teamGroups = new HashMap<>();

    @Override
    public void register() {
        BukkitVoicechatService service = BukkitPlugin.INSTANCE.getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (service != null) {
            service.registerPlugin(this);
        }
        Bukkit.getPluginManager().registerEvents(this, BukkitPlugin.INSTANCE);
        Log.info("Hooked 'Simple Voice Chat' for Voice Chat support.");
    }

    @Override
    public String getPluginId() {
        return BukkitPlugin.INSTANCE.getName();
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::onSpectatorSpeak);
    }

    public void onSpectatorSpeak(MicrophonePacketEvent event) {
        VoicechatConnection senderConn = event.getSenderConnection();
        VoicechatConnection receiverConn = event.getReceiverConnection();
        if (senderConn == null || receiverConn == null) return;

        Player sender = Bukkit.getPlayer(senderConn.getPlayer().getUuid());
        Player receiver = Bukkit.getPlayer(receiverConn.getPlayer().getUuid());
        if (sender == null || receiver == null) return;

        if (!UHCUtils.isAlive(sender.getUniqueId()) && UHCUtils.isAlive(receiver.getUniqueId())) {
            event.cancel();
        }
    }
}
