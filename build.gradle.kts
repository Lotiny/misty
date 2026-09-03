import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java plugin
    id("java-library")

    // Fairy framework plugin
    id("io.fairyproject") version "0.8.7b2-SNAPSHOT" apply false

    // Dependency management plugin
    id("io.spring.dependency-management") version "1.1.7"

    // Shadow plugin, provides the ability to shade fairy and other dependencies to compiled jar
    id("com.gradleup.shadow") version "9.5.1" apply false

    // Lombok plugin
    id("io.freefair.lombok") version "9.5.0" apply false

    // Spotless plugin
    id("com.diffplug.spotless") version "8.10.1"
}

allprojects {
    // Apply Shadow plugin
    pluginManager.apply("com.gradleup.shadow")

    // Configure repositories
    repositories {
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.codemc.io/repository/maven-public/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.imanity.dev/imanity-libraries")
        maven("https://mvn.wesjd.net/")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

subprojects {
    // Apply necessary plugins
    pluginManager.apply("java-library")
    pluginManager.apply("io.fairyproject")
    pluginManager.apply("io.spring.dependency-management")
    pluginManager.apply("com.gradleup.shadow")
    pluginManager.apply("io.freefair.lombok")

    // Configure dependencies
    dependencies {
        api("io.fairyproject:bukkit-bundles")
        compileOnlyApi("io.fairyproject:bukkit-command")
        compileOnlyApi("io.fairyproject:bukkit-gui")
        compileOnlyApi("io.fairyproject:bukkit-xseries")
        compileOnlyApi("io.fairyproject:bukkit-items")
        compileOnlyApi("io.fairyproject:bukkit-visibility")
        compileOnlyApi("io.fairyproject:bukkit-visual")
        compileOnlyApi("io.fairyproject:bukkit-timer")
        compileOnlyApi("io.fairyproject:bukkit-nbt")
        compileOnlyApi("io.fairyproject:mc-hologram")
        compileOnlyApi("io.fairyproject:mc-nametag")
        compileOnlyApi("io.fairyproject:mc-sidebar")
    }

    // Spotless configuration
    if (project.file("src").exists()) {
        pluginManager.apply("com.diffplug.spotless")

        spotless {
            java {
                palantirJavaFormat()

                formatAnnotations()
                removeUnusedImports()

                val importOrder = rootProject.file(".spotless/misty.importorder")
                if (importOrder.exists()) {
                    importOrderFile(importOrder)
                }

                trimTrailingWhitespace()
                endWithNewline()
            }
        }
    }

    // Configure ShadowJar task
    tasks.withType(ShadowJar::class) {
        archiveFileName.set("Misty-${properties("version")}.jar")

        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        // Relocate fairy to avoid plugin conflict
        relocate("io.fairyproject", "${properties("group")}.fairy")
        relocate("io.fairyproject.bukkit.menu", "${properties("group")}.fairy.menu")

        relocate("net.kyori", "${properties("group")}.libs.kyori")
        relocate("com.cryptomorin.xseries", "${properties("group")}.libs.xseries")
        relocate("org.yaml.snakeyaml", "${properties("group")}.libs.snakeyaml")
        relocate("com.google.gson", "${properties("group")}.libs.gson")
        relocate("com.github.retrooper.packetevents", "${properties("group")}.libs.packetevents")
        relocate("io.github.retrooper.packetevents", "${properties("group")}.libs.packetevents")
        relocate("net.wesjd.anvilgui", "${properties("group")}.libs.anvilgui")
        relocate("de.exlll.configlib", "${properties("group")}.libs.configlib")

        archiveClassifier.set("plugin")
        mergeServiceFiles()
        exclude("META-INF/maven/**")
    }
}