package me.lotiny.misty.bukkit.game;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import io.fairyproject.Fairy;
import io.fairyproject.bukkit.metadata.Metadata;
import io.fairyproject.bukkit.visibility.VisibilityService;
import io.fairyproject.container.Autowired;
import io.fairyproject.container.DependsOn;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.container.PostInitialize;
import io.fairyproject.log.Log;
import io.fairyproject.mc.MCPlayer;
import io.fairyproject.mc.nametag.NameTagService;
import io.fairyproject.mc.scheduler.MCSchedulers;
import io.fairyproject.util.CC;
import lombok.Getter;
import me.lotiny.misty.api.event.FinalHealExecutedEvent;
import me.lotiny.misty.api.event.GracePeriodEndEvent;
import me.lotiny.misty.api.game.Game;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameSetting;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.game.registry.CombatLogger;
import me.lotiny.misty.api.game.registry.GameRegistry;
import me.lotiny.misty.api.profile.Profile;
import me.lotiny.misty.api.profile.stats.StatType;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.team.Team;
import me.lotiny.misty.api.team.TeamManager;
import me.lotiny.misty.bukkit.config.Config;
import me.lotiny.misty.bukkit.config.ConfigManager;
import me.lotiny.misty.bukkit.config.impl.MainConfig;
import me.lotiny.misty.bukkit.config.impl.UHCConfig;
import me.lotiny.misty.bukkit.game.registry.GameRegistryImpl;
import me.lotiny.misty.bukkit.hook.PluginHookManager;
import me.lotiny.misty.bukkit.hook.impl.apollo.ApolloHook;
import me.lotiny.misty.bukkit.storage.StorageRegistry;
import me.lotiny.misty.bukkit.task.AnnounceTask;
import me.lotiny.misty.bukkit.utils.*;
import me.lotiny.misty.bukkit.utils.cooldown.CombatLoggerCooldown;
import me.lotiny.misty.shared.recipe.MistyShapedRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@DependsOn(ConfigManager.class)
@InjectableComponent
public class GameManagerImpl implements GameManager {

    @Autowired
    private static TeamManager teamManager;
    @Autowired
    private static PluginHookManager pluginHookManager;
    @Autowired
    private static ScenarioManager scenarioManager;
    @Autowired
    private static StorageRegistry storageRegistry;
    @Autowired
    private static NameTagService nameTagService;
    @Autowired
    private static VisibilityService visibilityService;

    @Getter
    private final Map<UUID, GameSetting> gameSettingMap = new ConcurrentHashMap<>();
    @Getter
    private GameRegistry registry;
    @Getter
    private Game game;
    private UUID settingToLoad;
    @Getter
    private CombatLoggerCooldown combatLoggerCooldown;

    @PostInitialize
    public void onPostInit() {
        MainConfig mainConfig = Config.getMainConfig();
        this.registry = new GameRegistryImpl(mainConfig);
        this.combatLoggerCooldown = new CombatLoggerCooldown((mainConfig.getLogoutTimer() * 60L) * 1000L, CombatLogger::remove);
        setupScoreboardTask();

        scenarioManager.registerScenarios();
        loadSettings();
        loadGame(settingToLoad);

        if (mainConfig.getAutoStart().isEnabled() && mainConfig.getAutoStart().getAnnounce().isEnabled()) {
            new AnnounceTask().run(true, 20L);
        }
    }

    private void setupScoreboardTask() {
        MCSchedulers.getAsyncScheduler().scheduleAtFixedRate(() -> {
            Bukkit.getOnlinePlayers().forEach(this::processPlayer);
            ApolloHook apolloHook = pluginHookManager.getApolloHook();
            if (apolloHook != null) {
                teamManager.getTeams().values().forEach(apolloHook::refreshTeam);
            }
        }, 2L, 2L);
    }

    private void processPlayer(Player player) {
        MCSchedulers.getGlobalScheduler().schedule(() -> {
            handlePlayerScoreboard(player);
            visibilityService.update(player);
            MCPlayer mcPlayer = MCPlayer.from(player);
            if (mcPlayer != null && Config.getMainConfig().getNameTag().isEnabled()) {
                nameTagService.update(mcPlayer);
            }
        });
    }

    private void handlePlayerScoreboard(Player player) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scenarioManager.isEnabled("Secret Health") || !Config.getMainConfig().isHealthBelowName())
            return;

        Scoreboard board = getOrCreateScoreboard(player, scoreboardManager);
        Objective objective = getOrCreateObjective(board);

        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        //noinspection deprecation
        objective.setDisplayName(CC.RED + "❤");
    }

    private Scoreboard getOrCreateScoreboard(Player player, ScoreboardManager scoreboardManager) {
        Scoreboard board = player.getScoreboard();
        if (board == scoreboardManager.getMainScoreboard()) {
            Scoreboard newBoard = scoreboardManager.getNewScoreboard();
            player.setScoreboard(newBoard);
            return newBoard;
        }
        return board;
    }

    private Objective getOrCreateObjective(Scoreboard board) {
        Objective objective = board.getObjective(DisplaySlot.BELOW_NAME);
        if (objective == null) {
            objective = ReflectionUtils.get().registerHealthObjective(board);
        }
        return objective;
    }

    private void loadSettings() {
        for (Map.Entry<UUID, UHCConfig.GameConfig> entry : Config.getUhcConfig().getGameConfig().entrySet()) {
            UUID id = entry.getKey();
            UHCConfig.GameConfig gameConfig = entry.getValue();

            GameSettingImpl loadedSetting = new GameSettingImpl(id);
            loadedSetting.setConfigName(gameConfig.getName());
            loadedSetting.setSavedBy(gameConfig.getSavedBy());
            loadedSetting.setSavedDate(gameConfig.getSavedDate().toString());
            loadedSetting.setTeamSize(gameConfig.getSetting().getTeamSize());
            loadedSetting.setFinalHeal(gameConfig.getSetting().getFinalHeal());
            loadedSetting.setGracePeriod(gameConfig.getSetting().getGracePeriod());
            loadedSetting.setBorderSize(gameConfig.getSetting().getBorderSize());
            loadedSetting.setFirstShrink(gameConfig.getSetting().getFirstShrink());
            loadedSetting.setNetherTime(gameConfig.getSetting().getNetherTime());
            loadedSetting.setAppleRate(gameConfig.getSetting().getAppleRate());
            loadedSetting.setLastBorderFlat(gameConfig.getSetting().isLastBorderFlat());
            loadedSetting.setLateScatter(gameConfig.getSetting().isLateScatter());
            loadedSetting.setShears(gameConfig.getSetting().isShears());
            loadedSetting.setGodApple(gameConfig.getSetting().isGodApple());
            loadedSetting.setPearlDamage(gameConfig.getSetting().isEnderPearlDamage());
            loadedSetting.setChatBeforePvp(gameConfig.getSetting().isChatBeforePvp());
            loadedSetting.setNether(gameConfig.getSetting().isNether());
            loadedSetting.setBedBomb(gameConfig.getSetting().isBedBomb());
            loadedSetting.setSpeed1(gameConfig.getPotion().isSpeed1());
            loadedSetting.setSpeed2(gameConfig.getPotion().isSpeed2());
            loadedSetting.setStrength1(gameConfig.getPotion().isStrength1());
            loadedSetting.setStrength2(gameConfig.getPotion().isStrength2());
            loadedSetting.setPoison(gameConfig.getPotion().isPoison());
            loadedSetting.setInvisible(gameConfig.getPotion().isInvisible());
            loadedSetting.setEnabledScenarios(gameConfig.getScenarios());
            loadedSetting.setDef(gameConfig.isDefault());
            loadedSetting.setLoaded(gameConfig.isDefault());

            if (loadedSetting.isLoaded()) {
                settingToLoad = id;
            }
            this.gameSettingMap.put(id, loadedSetting);
        }
    }

    @Override
    public void loadGame(UUID configId) {
        GameSetting loadedSetting = this.gameSettingMap.get(configId);
        if (loadedSetting == null) {
            Log.warn("Failed to find game with ID '" + configId + "' in games list.");
            return;
        }

        this.game = new GameImpl(loadedSetting);

        scenarioManager.getEnabledScenarios(registry.getStartTask()).forEach(scenario -> scenarioManager.disable(scenario, this, Bukkit.getConsoleSender(), false));

        loadedSetting.getEnabledScenarios().forEach(s -> {
            Scenario scenario = scenarioManager.getScenario(s);
            if (scenario == null) {
                Log.warn("Failed to find Scenario '" + s + "' in scenarios list.");
                return;
            }

            scenarioManager.enable(scenario, this, Bukkit.getConsoleSender(), false);
        });

        Log.info("Loaded game ID: " + loadedSetting.getConfigId() + "!");
    }

    @Override
    public void saveGame(GameSetting setting) {
        Config.getUhcConfig().getGameConfig().put(
                setting.getConfigId(),
                new UHCConfig.GameConfig(
                        setting.getConfigName(),
                        setting.isDef(),
                        setting.getEnabledScenarios(),
                        new UHCConfig.Potion(
                                setting.isSpeed1(),
                                setting.isSpeed2(),
                                setting.isStrength1(),
                                setting.isStrength2(),
                                setting.isPoison(),
                                setting.isInvisible()
                        ),
                        new UHCConfig.Setting(
                                setting.getTeamSize(),
                                setting.getFinalHeal(),
                                setting.getGracePeriod(),
                                setting.getBorderSize(),
                                setting.getFirstShrink(),
                                setting.isLastBorderFlat(),
                                setting.getAppleRate(),
                                setting.isShears(),
                                setting.isLateScatter(),
                                setting.isGodApple(),
                                setting.isPearlDamage(),
                                setting.isChatBeforePvp(),
                                setting.isNether(),
                                setting.getNetherTime(),
                                setting.isBedBomb()
                        ),
                        setting.getSavedBy(),
                        LocalDate.parse(setting.getSavedDate())
                )
        );
        Config.getUhcConfig().save();
    }

    @Override
    public void addGoldenHeadRecipe() {
        Bukkit.getServer().addRecipe(
                ReflectionUtils.get().createShapedRecipe(
                        MistyShapedRecipe.builder()
                                .namespace("golden-head")
                                .result(GoldenHead.build())
                                .shape("AAAABAAAA")
                                .ingredients(Map.of(
                                        'A', XMaterial.GOLD_INGOT,
                                        'B', XMaterial.PLAYER_HEAD
                                ))
                                .build()
                )
        );
    }

    @Override
    public void registerPlayerDeath(Player player) {
        Snapshot snapshot = new Snapshot(player.getUniqueId());
        snapshot.setLevel(player.getLevel());
        snapshot.setExp(player.getExp());
        snapshot.setHealth(player.getHealth());
        snapshot.setMaxHealth(PlayerUtils.getMaxHealth(player));
        snapshot.setItems(player.getInventory().getContents());
        snapshot.setArmors(player.getInventory().getArmorContents());
        snapshot.setLocation(player.getLocation());
        snapshot.setTeam(UHCUtils.getTeam(player));
        snapshot.getEffects().addAll(player.getActivePotionEffects());

        Metadata.provideForPlayer(player).put(KeyEx.SNAPSHOT_KEY, snapshot);
    }

    @Override
    public Location findSafeScatterLocation(World world, int size) {
        int x;
        int z;
        int y;

        do {
            x = Fairy.random().nextInt(-size + 1, size - 1);
            z = Fairy.random().nextInt(-size + 1, size - 1);
            y = world.getHighestBlockYAt(x, z);
        } while (y <= 55 || y >= 80);

        return new Location(world, x, y, z);
    }

    @Override
    public void teleportToRandomLocation(Player player, int size) {
        World world = Bukkit.getWorld(registry.getUhcWorld());
        if (world != null) {
            int x = Fairy.random().nextInt(-size, size);
            int z = Fairy.random().nextInt(-size, size);
            int y = world.getHighestBlockYAt(x, z);

            Location location = new Location(world, x, y, z);
            UHCUtils.getTeam(player).getStorage().put(TeamEx.SCATTER_LOCATION, location);
            player.teleport(location);
        }
    }

    @Override
    public boolean checkWinner() {
        if (registry.getState() != GameState.INGAME) {
            return false;
        }

        Map<Integer, Team> teams = teamManager.getTeams();
        if (teams.size() != 1) {
            return false;
        }

        this.game.stop();
        return true;
    }

    @Override
    public void executeFinalHeal() {
        this.registry.getAlivePlayers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                PlayerUtils.healPlayer(player);
                player.setFoodLevel(20);
            }
        });
        this.registry.setFinalHealHappened(true);
        PlayerUtils.playSound(XSound.ENTITY_EXPERIENCE_ORB_PICKUP);
        Utilities.broadcast("&bAll players have been healed.");

        MCSchedulers.getGlobalScheduler().schedule(() -> Bukkit.getPluginManager().callEvent(new FinalHealExecutedEvent()));
    }

    @Override
    public void endGracePeriod() {
        this.registry.setPvpEnabled(true);

        PlayerUtils.playSound(XSound.ENTITY_EXPERIENCE_ORB_PICKUP);
        Utilities.broadcast("&bPvP is now enabled.");

        if (!this.game.getSetting().isChatBeforePvp() && this.registry.isChatMuted()) {
            this.registry.setChatMuted(false);
            Utilities.broadcast("&aThe chat is now un-muted!");
        }

        MCSchedulers.getGlobalScheduler().schedule(() -> Bukkit.getPluginManager().callEvent(new GracePeriodEndEvent()), 1L);
    }

    @Override
    public Optional<CombatLogger> findCombatLogger(Player player) {
        if (player == null) {
            return Optional.empty();
        }

        return registry.getCombatLoggers().values().stream()
                .filter(combatLogger -> combatLogger.getPlayerUniqueId().equals(player.getUniqueId()))
                .findFirst();
    }

    @Override
    public void disqualifyPlayer(UUID uuid) {
        Profile profile = storageRegistry.getProfile(uuid);
        Team team = Metadata.provideForPlayer(uuid).getOrThrow(KeyEx.SNAPSHOT_KEY).getTeam();

        this.registry.getPlayers().replace(uuid, false);

        if (team.getMembers(true).isEmpty()) {
            teamManager.getTeams().remove(team.getId());
        }
        profile.getStats(StatType.DEATHS).increase();

        Utilities.broadcast(profile.getName() + " has died");
        checkWinner();
    }
}
