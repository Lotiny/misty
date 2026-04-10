import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java plugin
    id("java-library")

    // Fairy framework plugin
    id("io.fairyproject") version "0.8.6b1-SNAPSHOT" apply false

    // Dependency management plugin
    id("io.spring.dependency-management") version "1.1.0"

    // Shadow plugin, provides the ability to shade fairy and other dependencies to compiled jar
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false

    // Lombok plugin
    id("io.freefair.lombok") version "9.1.0" apply false

    // Spotless plugin
    id("com.diffplug.spotless") version "8.2.1"
}

allprojects {
    // Apply Shadow plugin
    apply(plugin = "com.github.johnrengelman.shadow")

    // Configure repositories
    repositories {
        mavenCentral()
        maven(url = uri("https://oss.sonatype.org/content/repositories/snapshots/"))
        maven(url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/"))
        maven(url = uri("https://repo.codemc.io/repository/maven-public/"))
        maven(url = uri("https://repo.papermc.io/repository/maven-public/"))
        maven(url = uri("https://repo.imanity.dev/imanity-libraries"))
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

subprojects {
    // Apply necessary plugins
    apply(plugin = "java-library")
    apply(plugin = "io.fairyproject")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "io.freefair.lombok")

    // Configure dependencies
    dependencies {
        api("io.fairyproject:bukkit-bundles")
        api("io.fairyproject:bukkit-command")
        api("io.fairyproject:bukkit-gui")
        api("io.fairyproject:bukkit-xseries")
        api("io.fairyproject:bukkit-items")
        api("io.fairyproject:bukkit-visibility")
        api("io.fairyproject:bukkit-visual")
        api("io.fairyproject:bukkit-timer")
        api("io.fairyproject:bukkit-nbt")
        api("io.fairyproject:mc-hologram")
        api("io.fairyproject:mc-nametag")
        api("io.fairyproject:mc-sidebar")
    }

    // Spotless configuration
    if (project.file("src").exists()) {
        apply(plugin = "com.diffplug.spotless")

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

        // Relocate fairy to avoid plugin conflict
        relocate("io.fairyproject", "${properties("group")}.fairy")
        relocate("net.kyori", "${properties("group")}.libs.kyori")
        relocate("com.cryptomorin.xseries", "${properties("group")}.libs.xseries")
        relocate("org.yaml.snakeyaml", "${properties("group")}.libs.snakeyaml")
        relocate("com.google.gson", "${properties("group")}.libs.gson")
        relocate("com.github.retrooper.packetevents", "${properties("group")}.libs.packetevents")
        relocate("io.github.retrooper.packetevents", "${properties("group")}.libs.packetevents")
        relocate("net.wesjd.anvilgui", "${properties("group")}.libs.anvilgui")
        relocate("de.exlll.configlib", "${properties("group")}.libs.configlib")

        relocate("io.fairyproject.bukkit.menu", "${properties("group")}.fairy.menu")

        archiveClassifier.set("plugin")
        mergeServiceFiles()
        exclude("META-INF/maven/**")
    }
}