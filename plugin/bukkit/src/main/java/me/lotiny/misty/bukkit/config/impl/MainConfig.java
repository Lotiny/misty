package me.lotiny.misty.bukkit.config.impl;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.SerializeWith;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.lotiny.misty.bukkit.config.BaseConfig;
import me.lotiny.misty.bukkit.config.serializer.KitSerializer;
import me.lotiny.misty.bukkit.config.serializer.LocationSerializer;
import me.lotiny.misty.bukkit.config.serializer.PotionEffectSerializer;
import me.lotiny.misty.bukkit.kit.Kit;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Configuration
public final class MainConfig extends BaseConfig {

    @SerializeWith(serializer = LocationSerializer.class)
    @Comment("The location where players will spawn while waiting for the game to start.")
    private Location lobbyLocation = new Location(Bukkit.getWorlds().getFirst(), 0, 100, 0);

    @Comment("The time in minutes before scattering begins when the whitelist will automatically be disabled.")
    private int whitelistOffBefore = 10;

    @Comment("The time in minutes to remove a disconnected player from the game after they log out.")
    private int logoutTimer = 10;

    @Comment("If true, the chat will be cleared when the game starts.")
    private boolean clearChatOnStart = true;

    @Comment("The timezone of your server, used for handling time-based systems.")
    private String timeZone = "Asia/Bangkok";

    @Comment("Choose the size for pre generate the world 500 means 500x500 from the center x:0, z:0 (Use -1 for using current border size).")
    private int preGenerateWorldSize = 500;

    @Comment("The countdown after scattering finishes. This is recommended at 20 seconds to let the server's TPS stabilize.")
    private int stabilizeSeconds = 20;

    @Comment({
            "Defines the scale for nether compare to the overworld.",
            "Normally 8 blocks in nether is equals 1 blocks in overworld (8:1).",
            "NOTE: It is recommended to set it to 2 for UHC gameplay (2:1)."
    })
    private int netherScale = 2;

    @Comment("Set to true if you want to enable hearts below player names.")
    private boolean healthBelowName = true;

    @Comment("When enabled a lightning strike will happen on the place of the player death.")
    private boolean strikeLightningOnDeath = true;

    @Comment("Settings related to game worlds.")
    private World world = new World();

    @Comment("The message that will be sent when a player joins the server.")
    private List<String> joinMessage = List.of(
            "<gray><st>-------------------------",
            "<white>Host<gray>: <aqua><host>",
            "<white>Game Type<gray>: <aqua><type>",
            "<white>Border<gray>: <aqua><border>",
            "<gray><st>-------------------------"
    );

    @Comment("Customize the kit player players.")
    private KitConfig kit = new KitConfig();

    @Comment("Customize the autostart system to match your server.")
    private AutoStart autoStart = new AutoStart();

    @Comment("Settings for the world border.")
    private Border border = new Border();

    @Comment("Manage whitelisted players or the whitelist status.")
    private WhiteList whiteList = new WhiteList();

    @Comment("Customize your healing items (Golden Apple, Player Head, and Golden Head).")
    private Healing healing = new Healing();

    @Comment("Settings related to nametag features.")
    private NameTag nameTag = new NameTag();

    @Comment("Settings related to chat format features.")
    private ChatFormat chatFormat = new ChatFormat();

    @Comment("Settings related to chat prefix features.")
    private ChatPrefix chatPrefix = new ChatPrefix();

    @Getter
    @Configuration
    public static class AutoStart {

        @Comment("If true, the AutoStart feature will be enabled.")
        private boolean enabled = false;

        @Comment("The minimum number of players required before the start countdown begins.")
        private int minPlayers = 20;

        @Comment("Countdown time (in seconds) before the game starts once the minimum player count is reached.")
        private int timer = 180;

        @Comment("If true, the countdown will be canceled if the player count drops below the minimum required.")
        private boolean canceled = true;

        @Comment("The section for announcement message.")
        private Announce announce = new Announce();

        @Getter
        @Configuration
        public static class Announce {

            @Comment("If true, a message will be sent showing how many more players are needed for AutoStart.")
            private boolean enabled = true;

            @Comment("The interval (in seconds) between announcement messages.")
            private int interval = 30;

            @Comment("The announcement message that will be sent.")
            private String message = "<prefix> &c<required> more players are required to start!";
        }
    }

    @Getter
    @Setter
    @Configuration
    public static class KitConfig {

        @Comment("If true, the kit system will be enable.")
        private boolean enabled = false;

        @Comment("The default kit that will given to player when random kit is disabled.")
        private int defaultKit = 0;

        @Comment("Make each player get randomly kit.")
        private boolean randomKit = false;

        @SerializeWith(serializer = KitSerializer.class, nesting = 1)
        @Comment("Available kits.")
        private Map<Integer, Kit> kits = Map.of(
                0, new Kit(
                        new ItemStack[4],
                        Arrays.copyOf(new ItemStack[]{
                                ItemStackUtils.of(XMaterial.COOKED_BEEF, 10)
                        }, 36)
                )
        );
    }

    @Getter
    @Setter
    @Configuration
    public static class World {

        @Comment("The name of the main game world.")
        private String game = "uhc_game";

        @Comment("The name of the nether world.")
        private String nether = "uhc_nether";

        @Comment("Do not touch unless you know what this does.")
        private boolean loaded = false;

        @Comment("Do not touch unless you know what this does.")
        private boolean played = true;
    }

    @Getter
    @Configuration
    public static class Border {

        @Comment("The block material to use for the world border wall.")
        private XMaterial block = XMaterial.BEDROCK;

        @Comment("The height of the world border wall.")
        private int height = 4;

        @Comment("The time in minutes between each border shrink.")
        private int interval = 5;

        @Comment("A list of allowed border sizes that can be set and shrunk to.")
        private List<Integer> allowedSize = List.of(25, 50, 100, 500, 1000, 2000, 3000);

        @Comment("Settings for the visual border to prevent players from going outside (Use 'AIR' to disabled).")
        private XMaterial visualBorder = XMaterial.RED_STAINED_GLASS;

        @Comment("The border size at which random teleportation should begin (Use -1 to disabled).")
        private int randomTeleport = 500;
    }

    @Getter
    @Configuration
    public static class WhiteList {

        @Comment("If true, the whitelist will be enabled when the server starts.")
        private boolean enabledWhitelist = true;

        @Comment("A list of players who are pre-whitelisted.")
        private List<String> players = List.of("Lotiny", "MizukiRinka");
    }

    @Getter
    @Configuration
    public static class Healing {

        @Comment("Settings for Golden Apples.")
        private HealingItem goldenApple = new HealingItem(
                false,
                null,
                null,
                1.6F,
                null,
                List.of(
                        XPotion.REGENERATION.buildPotionEffect(200, 2),
                        XPotion.ABSORPTION.buildPotionEffect(3000, 1)
                )
        );

        @Comment("Settings for Player Heads.")
        private HealingItem playerHead = new HealingItem(
                false,
                null,
                null,
                0.0F,
                null,
                List.of(
                        XPotion.SPEED.buildPotionEffect(280, 2),
                        XPotion.REGENERATION.buildPotionEffect(100, 2)
                )
        );

        @Comment("Settings for Golden Heads.")
        private HealingItem goldenHead = new HealingItem(
                true,
                XMaterial.PLAYER_HEAD,
                "http://textures.minecraft.net/texture/2a26a2e579ad480e90f89ec04cda38e5662b95d113d6f2da34846f892ffc2e5b",
                0.5F,
                "&6&lGolden Head",
                List.of(
                        XPotion.REGENERATION.buildPotionEffect(200, 3),
                        XPotion.ABSORPTION.buildPotionEffect(3000, 1)
                )
        );

        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        @Configuration
        public static class HealingItem {

            @Comment("If true, the custom potion effects below will be applied.")
            private boolean enabled;

            @Comment("The material for the Golden Head item (e.g., GOLDEN_APPLE, PLAYER_HEAD).")
            private XMaterial material;

            @Comment("Skin URL for the head texture. This is only used if the material is set to PLAYER_HEAD.")
            private String skinUrl;

            @Comment("How long player consume this item in seconds (Only work on 1.21.4+).")
            private float time;

            @Comment("The display name of the item.")
            private String name;

            @SerializeWith(serializer = PotionEffectSerializer.class, nesting = 1)
            @Comment("Potion effects applied when this item is consumed. Format: EFFECT|DURATION_IN_TICKS|AMPLIFIER")
            private List<PotionEffect> potionEffects;
        }
    }

    @Getter
    @Configuration
    public static class NameTag {

        @Comment("If true, nametags will be colored based on player status.")
        private boolean enabled = true;

        @Comment("The prefix to display on nametags in team games.")
        private String teamPrefix = "[<team>]";

        @Comment("The color for friendly players.")
        private String friendly = "&a";

        @Comment("The color for enemies.")
        private String enemy = "&c";

        @Comment("The color for 'no clean' players.")
        private String noClean = "&6";

        @Comment("The color for 'do not disturb' players.")
        private String doNotDisturb = "&d";

        @Comment("The color for spectators.")
        private String spectator = "&7";
    }

    @Getter
    @Configuration
    public static class ChatFormat {

        @Comment("If true, the default chat format will be overridden by these formats.")
        private boolean enabled = true;

        @Comment("The chat format for normal gameplay.")
        private String normal = "<prefix><player><suffix>&7: &f<message>";

        @Comment("The chat format for team messages.")
        private String team = "&7[&aTeam-Chat&7] &r<prefix><player><suffix>&7: &f<message>";

        @Comment("The chat format for spectators.")
        private String spectator = "&7[Spectate&7] &r<prefix><player><suffix>&7: &f<message>";
    }

    @Getter
    @Configuration
    public static class ChatPrefix {

        @Comment("The prefix for host players.")
        private String hostPrefix = "&7[&6Host&7] &r";

        @Comment("The prefix for moderator players.")
        private String modPrefix = "&7[&5Mod&7] &r";

        @Comment("The team prefix to use in team games.")
        private String teamPrefix = "&7[&a#<id>&7] &r";
    }
}