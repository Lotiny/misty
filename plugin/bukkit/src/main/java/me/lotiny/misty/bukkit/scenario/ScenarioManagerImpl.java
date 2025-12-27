package me.lotiny.misty.bukkit.scenario;

import io.fairyproject.bootstrap.bukkit.BukkitPlugin;
import io.fairyproject.container.Autowired;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.util.CC;
import lombok.Getter;
import me.lotiny.misty.api.game.ConfigType;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.task.AbstractScheduleTask;
import me.lotiny.misty.api.team.TeamManager;
import me.lotiny.misty.bukkit.enums.Message;
import me.lotiny.misty.bukkit.scenario.annotations.IncompatibleWith;
import me.lotiny.misty.bukkit.scenario.annotations.Required;
import me.lotiny.misty.bukkit.scenario.impl.*;
import me.lotiny.misty.bukkit.utils.StringUtils;
import me.lotiny.misty.bukkit.utils.UHCUtils;
import me.lotiny.misty.bukkit.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@InjectableComponent
public class ScenarioManagerImpl implements ScenarioManager {

    @Autowired
    private static TeamManager teamManager;

    private final List<Scenario> scenarios = new ArrayList<>();
    private final List<Scenario> enabledScenarios = new ArrayList<>();
    private final List<String> scenariosToEnable = new ArrayList<>();
    @Getter
    private final List<ItemStack> droppedItems = new ArrayList<>();

    @Override
    public void registerScenarios() {
        List.of(
                new CutCleanScenario(),
                new NoCleanScenario(),
                new TimberScenario(),
                new TimebombScenario(),
                new SafelootScenario(),
                new HasteyBabiesScenario(),
                new HasteyBoysScenario(),
                new HasteyMenScenario(),
                new DoNotDisturbScenario(),
                new FlowerPowerScenario(),
                new RandomizerScenario(),
                new BackpacksScenario(),
                new DoubleOreScenario(),
                new TripleOreScenario(),
                new DoubleExpScenario(),
                new TripleExpScenario(),
                new LoveAtFirstSightScenario(),
                new RedVsBlueScenario(),
                new CompanionBenchScenario(),
                new HairySheepScenario(),
                new TrackerScenario(),
                new DoubleHealthScenario(),
                new AbsorptionlessScenario(),
                new AbsorptionPartnerScenario(),
                new BedBombScenario(),
                new BarebonesScenario(),
                new DiamondlessScenario(),
                new GoldlessScenario(),
                new AxelessScenario(),
                new CustomCraftScenario(),
                new NoFallScenario(),
                new WebcageScenario(),
                new LuckyLeavesScenario(),
                new PearlGiverScenario(),
                new PermanightScenario(),
                new BlockedScenario(),
                new WebLimitScenario(),
                new BaldChickenScenario(),
                new BatsScenario(),
                new ArcaneArchivesScenario(),
                new ForbiddenAlchemyScenario(),
                new BleedingSweetsScenario(),
                new BlockRushScenario(),
                new BowlessScenario(),
                new ChumpCharityScenario(),
                new ColdWeaponScenario(),
                new BetterEnchantScenario(),
                new CreeperPongScenario(),
                new CupidScenario(),
                new EggsScenario(),
                new ShieldlessScenario(),
                new BoneBreakerScenario(),
                new UltraParanoidScenario(),
                new StockUpScenario(),
                new FirelessScenario(),
                new EnchantlessScenario(),
                new EntropyScenario(),
                new BloodDiamondsScenario(),
                new FrozenInTimeScenario(),
                new GapZapScenario(),
                new GoldRushScenario(),
                new GoneFishingScenario(),
                new CentralEnchantmentScenario(),
                new BirdScenario(),
                new HomeworkScenario(),
                new KillSwitchScenario(),
                new LightOutScenario(),
                new PlayerSwapScenario(),
                new PuppyPowerScenario(),
                new PyroScenario(),
                new VengefulSpiritScenario(),
                new RodlessScenario(),
                new SecretHealthScenario(),
                new SheepLoversScenario(),
                new SkyHighScenario(),
                new SoupScenario(),
                new SpeedDemonScenario(),
                new SwitcherooScenario(),
                new SwordlessScenario(),
                new ZoomiesScenario()
        ).forEach(scenario -> {
            if (scenario.shouldRegister()) {
                scenarios.add(scenario);
            }
        });
    }

    @Override
    public void dropScenarioItems(Location location) {
        for (ItemStack item : this.droppedItems) {
            UHCUtils.dropItem(location, item);
        }
    }

    @Override
    public List<Scenario> getEnabledScenarios(GameManager gameManager) {
        AbstractScheduleTask startTask = gameManager.getRegistry().getStartTask();
        return this.getEnabledScenarios(startTask);
    }

    @Override
    public List<Scenario> getEnabledScenarios(AbstractScheduleTask startTask) {
        if (startTask == null || startTask.getSeconds() > 10) {
            return scenariosToEnable.stream()
                    .map(this::getScenario)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return this.enabledScenarios;
    }

    @Override
    public List<Scenario> getScenarios() {
        return this.scenarios;
    }

    @Override
    public List<String> getScenariosToEnable() {
        return this.scenariosToEnable;
    }

    @Override
    public boolean isEnabled(String name) {
        return getScenario(name).isEnabled();
    }

    @Override
    public Scenario getScenario(String name) {
        return scenarios.stream()
                .filter(scenario -> StringUtils.rb(scenario.getName())
                        .equalsIgnoreCase(StringUtils.rb(name)))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void enable(Scenario scenario, GameManager gameManager, CommandSender sender, boolean messageLog) {
        if (scenario.getClass().isAnnotationPresent(IncompatibleWith.class)) {
            IncompatibleWith annotation = scenario.getClass().getAnnotation(IncompatibleWith.class);
            Scenario conflict = getTargetScenario(annotation.value(), true);
            if (conflict != null) {
                sender.sendMessage(CC.RED + "You cannot enable " + scenario.getName() + " while " + conflict.getName() + " is active.");
                return;
            }
        }

        if (scenario.getClass().isAnnotationPresent(Required.class)) {
            Required annotation = scenario.getClass().getAnnotation(Required.class);
            Scenario dependency = getTargetScenario(annotation.value(), false);
            if (dependency != null) {
                sender.sendMessage(CC.RED + scenario.getName() + " requires " + dependency.getName() + " to be enabled first.");
                return;
            }
        }

        if (scenario.equals(SkyHighScenario.class) && gameManager.getGame().getSetting().getBorderSize() <= 500) {
            sender.sendMessage(CC.RED + scenario.getName() + " scenario can't enabled while starting border size is lower than 500x500.");
            return;
        }

        if ((scenario.equals(BackpacksScenario.class) || scenario.equals(AbsorptionPartnerScenario.class)) && gameManager.getGame().getSetting().getTeamSize() == 1) {
            sender.sendMessage(CC.RED + scenario.getName() + " scenario can only enable in Team game.");
            return;
        }

        if (scenario.equals(RedVsBlueScenario.class) || scenario.equals(LoveAtFirstSightScenario.class)) {
            if (gameManager.getRegistry().getState() == GameState.LOBBY) {
                teamManager.getTeams().values().forEach(teamManager::deleteTeam);
            }
            gameManager.getGame().getSetting().setConfig(ConfigType.GAME_TYPE, 2, null);
        }

        if (messageLog) {
            Utilities.broadcast(Message.SCENARIO_ENABLED.toString()
                    .replace("<scenario>", scenario.getName())
                    .replace("<player>", (sender instanceof Player) ? sender.getName() : "Console"));
        }

        scenario.setEnabled(true);

        if (gameManager.getRegistry().getState() == GameState.LOBBY) {
            scenariosToEnable.add(scenario.getName());
            return;
        }

        enabledScenarios.add(scenario);
        droppedItems.addAll(scenario.getDroppedItems());
        BukkitPlugin.INSTANCE.getServer().getPluginManager().registerEvents(scenario, BukkitPlugin.INSTANCE);

        scenario.onEnable();
    }

    @Override
    public void disable(Scenario scenario, GameManager gameManager, CommandSender sender, boolean messageLog) {
        for (Scenario activeScenario : enabledScenarios) {
            if (activeScenario.equals(scenario)) {
                continue;
            }

            if (activeScenario.getClass().isAnnotationPresent(Required.class)) {
                Required annotation = activeScenario.getClass().getAnnotation(Required.class);

                if (isScenarioInArray(scenario.getClass(), annotation.value())) {
                    disable(activeScenario, gameManager, Bukkit.getConsoleSender(), true);
                }
            }
        }

        if (messageLog) {
            Utilities.broadcast(Message.SCENARIO_DISABLED.toString()
                    .replace("<scenario>", scenario.getName())
                    .replace("<player>", (sender instanceof Player) ? sender.getName() : "Console"));
        }

        scenario.setEnabled(false);

        GameState state = gameManager.getRegistry().getState();
        if (state == GameState.LOBBY || state == GameState.SCATTERING) {
            scenariosToEnable.remove(scenario.getName());
            return;
        }

        enabledScenarios.remove(scenario);
        droppedItems.removeAll(scenario.getDroppedItems());
        HandlerList.unregisterAll(scenario);

        scenario.onDisable();
    }

    private @Nullable Scenario getTargetScenario(Class<? extends Scenario>[] targets, boolean enabled) {
        for (Scenario scenario : scenarios) {
            for (Class<? extends Scenario> targetClass : targets) {
                if (scenario.equals(targetClass) && scenario.isEnabled() == enabled) {
                    return scenario;
                }
            }
        }
        return null;
    }

    private boolean isScenarioInArray(Class<? extends Scenario> target, Class<? extends Scenario>[] array) {
        for (Class<? extends Scenario> clazz : array) {
            if (clazz.equals(target)) {
                return true;
            }
        }
        return false;
    }
}
