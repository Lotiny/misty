package me.lotiny.misty.api.game;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface GameSetting {

    UUID getConfigId();

    void setConfigId(UUID configId);

    String getConfigName();

    void setConfigName(String configName);

    String getSavedBy();

    void setSavedBy(String savedBy);

    String getSavedDate();

    void setSavedDate(String savedDate);

    int getTeamSize();

    void setTeamSize(int teamSize);

    int getFinalHeal();

    void setFinalHeal(int finalHeal);

    int getGracePeriod();

    void setGracePeriod(int gracePeriod);

    int getBorderSize();

    void setBorderSize(int borderSize);

    int getFirstShrink();

    void setFirstShrink(int firstShrink);

    int getNetherTime();

    void setNetherTime(int netherTime);

    int getAppleRate();

    void setAppleRate(int appleRate);

    boolean isLastBorderFlat();

    void setLastBorderFlat(boolean lastBorderFlat);

    boolean isShears();

    void setShears(boolean shears);

    boolean isLateScatter();

    void setLateScatter(boolean lateScatter);

    boolean isGodApple();

    void setGodApple(boolean godApple);

    boolean isPearlDamage();

    void setPearlDamage(boolean pearlDamage);

    boolean isChatBeforePvp();

    void setChatBeforePvp(boolean chatBeforePvp);

    boolean isNether();

    void setNether(boolean nether);

    boolean isBedBomb();

    void setBedBomb(boolean bedBomb);

    boolean isSpeed1();

    void setSpeed1(boolean speed1);

    boolean isSpeed2();

    void setSpeed2(boolean speed2);

    boolean isStrength1();

    void setStrength1(boolean strength1);

    boolean isStrength2();

    void setStrength2(boolean strength2);

    boolean isPoison();

    void setPoison(boolean poison);

    boolean isInvisible();

    void setInvisible(boolean invisible);

    List<String> getEnabledScenarios();

    void setEnabledScenarios(List<String> enabledScenarios);

    boolean isDef();

    void setDef(boolean def);

    boolean isLoaded();

    void setLoaded(boolean loaded);

    void setMaxTeamSize(int size);

    void setConfig(ConfigType configType, Object value, @Nullable CommandSender sender);
}
