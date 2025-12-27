package me.lotiny.misty.bukkit.scenario.annotations;

import me.lotiny.misty.api.scenario.Scenario;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Required {

    Class<? extends Scenario>[] value();
}
