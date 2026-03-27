package me.lotiny.misty.scenario;

import com.google.common.reflect.ClassPath;
import io.fairyproject.bootstrap.bukkit.BukkitPlugin;
import io.fairyproject.container.Autowired;
import io.fairyproject.container.InjectableComponent;
import io.fairyproject.log.Log;
import io.fairyproject.util.CC;
import lombok.Getter;
import me.lotiny.misty.api.game.ConfigType;
import me.lotiny.misty.api.game.GameManager;
import me.lotiny.misty.api.game.GameState;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.api.team.TeamManager;
import me.lotiny.misty.scenario.annotations.IncompatibleWith;
import me.lotiny.misty.scenario.annotations.Required;
import me.lotiny.misty.scenario.impl.AbsorptionPartnerScenario;
import me.lotiny.misty.scenario.impl.BackpacksScenario;
import me.lotiny.misty.scenario.impl.LoveAtFirstSightScenario;
import me.lotiny.misty.scenario.impl.RedVsBlueScenario;
import me.lotiny.misty.scenario.impl.SkyHighScenario;
import me.lotiny.misty.utils.Message;
import me.lotiny.misty.utils.StringUtils;
import me.lotiny.misty.utils.UHCUtils;
import me.lotiny.misty.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@InjectableComponent
public class ScenarioManagerImpl implements ScenarioManager {

    @Autowired
    private static TeamManager teamManager;

    private final List<Scenario> scenarios = new ArrayList<>();
    private final List<String> scenariosToEnable = new ArrayList<>();

    @Getter
    private final List<ItemStack> droppedItems = new ArrayList<>();

    @Override
    public void registerScenarios() {
        try {
            ClassPath classPath = ClassPath.from(getClass().getClassLoader());

            classPath.getTopLevelClasses("me.lotiny.misty.bukkit.scenario.impl").forEach(classInfo -> {
                try {
                    Class<?> clazz = classInfo.load();

                    if (Scenario.class.isAssignableFrom(clazz)
                            && !clazz.isInterface()
                            && !Modifier.isAbstract(clazz.getModifiers())) {
                        Scenario scenario =
                                (Scenario) clazz.getDeclaredConstructor().newInstance();

                        if (scenario.shouldRegister()) {
                            scenarios.add(scenario);
                        }
                    }
                } catch (Exception e) {
                    Log.warn("Failed to load scenario: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            Log.warn("Failed to load scenarios: " + e.getMessage());
        }
    }

    @Override
    public void dropScenarioItems(Location location) {
        for (ItemStack item : this.droppedItems) {
            UHCUtils.dropItem(location, item);
        }
    }

    @Override
    public List<Scenario> getEnabledScenarios() {
        return scenariosToEnable.stream()
                .map(this::getScenario)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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
                .filter(scenario -> StringUtils.rb(scenario.getName()).equalsIgnoreCase(StringUtils.rb(name)))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void enable(Scenario scenario, GameManager gameManager, CommandSender sender, boolean messageLog) {
        if (scenario.getClass().isAnnotationPresent(IncompatibleWith.class)) {
            IncompatibleWith annotation = scenario.getClass().getAnnotation(IncompatibleWith.class);
            Scenario conflict = getTargetScenario(annotation.value(), true);
            if (conflict != null) {
                sender.sendMessage(CC.RED + "You cannot enable " + scenario.getName() + " while " + conflict.getName()
                        + " is active.");
                return;
            }
        }

        if (scenario.getClass().isAnnotationPresent(Required.class)) {
            Required annotation = scenario.getClass().getAnnotation(Required.class);
            Scenario dependency = getTargetScenario(annotation.value(), false);
            if (dependency != null) {
                sender.sendMessage(
                        CC.RED + scenario.getName() + " requires " + dependency.getName() + " to be enabled first.");
                return;
            }
        }

        if (scenario.equals(SkyHighScenario.class)
                && gameManager.getGame().getSetting().getBorderSize() <= 500) {
            sender.sendMessage(CC.RED + scenario.getName()
                    + " scenario can't enabled while starting border size is lower than 500x500.");
            return;
        }

        if ((scenario.equals(BackpacksScenario.class) || scenario.equals(AbsorptionPartnerScenario.class))
                && gameManager.getGame().getSetting().getTeamSize() == 1) {
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
            Utilities.broadcast(Message.SCENARIO_ENABLED
                    .replace("<scenario>", scenario.getName())
                    .replace("<player>", (sender instanceof Player) ? sender.getName() : "Console"));
        }

        scenario.setEnabled(true);

        if (gameManager.getRegistry().getState() == GameState.LOBBY) {
            scenariosToEnable.add(scenario.getName());
            return;
        }

        if (!scenariosToEnable.contains(scenario.getName())) {
            scenariosToEnable.add(scenario.getName());
        }

        droppedItems.addAll(scenario.getDroppedItems());
        BukkitPlugin.INSTANCE.getServer().getPluginManager().registerEvents(scenario, BukkitPlugin.INSTANCE);

        scenario.onEnable();
    }

    @Override
    public void disable(Scenario scenario, GameManager gameManager, CommandSender sender, boolean messageLog) {
        for (String activeScenarioName : scenariosToEnable) {
            Scenario activeScenario = getScenario(activeScenarioName);
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
            Utilities.broadcast(Message.SCENARIO_DISABLED
                    .replace("<scenario>", scenario.getName())
                    .replace("<player>", (sender instanceof Player) ? sender.getName() : "Console"));
        }

        scenario.setEnabled(false);

        GameState state = gameManager.getRegistry().getState();
        if (state == GameState.LOBBY || state == GameState.SCATTERING) {
            scenariosToEnable.remove(scenario.getName());
            return;
        }

        scenariosToEnable.remove(scenario.getName());
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
