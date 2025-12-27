import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    //Java plugin
    id("java-library")

    //Fairy framework plugin
    id("io.fairyproject") version "0.8.3b1-SNAPSHOT" apply false

    // Dependency management plugin
    id("io.spring.dependency-management") version "1.1.0"

    //Shadow plugin, provides the ability to shade fairy and other dependencies to compiled jar
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false

    // Lombok
    id("io.freefair.lombok") version "8.14" apply false
}

allprojects {
    // Apply Shadow plugin
    apply(plugin = "com.github.johnrengelman.shadow")

    // Configure repositories
    repositories {
        mavenCentral()
        maven(url = uri("https://oss.sonatype.org/content/repositories/snapshots/"))
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
        compileOnlyApi("io.fairyproject:bukkit-platform")
        api("io.fairyproject:bukkit-bootstrap")
        compileOnlyApi("io.fairyproject:mc-animation")
        compileOnlyApi("io.fairyproject:bukkit-command")
        compileOnlyApi("io.fairyproject:bukkit-gui")
        compileOnlyApi("io.fairyproject:mc-hologram")
        compileOnlyApi("io.fairyproject:bukkit-xseries")
        compileOnlyApi("io.fairyproject:bukkit-items")
        compileOnlyApi("io.fairyproject:mc-nametag")
        compileOnlyApi("io.fairyproject:mc-sidebar")
        compileOnlyApi("io.fairyproject:bukkit-visibility")
        compileOnlyApi("io.fairyproject:bukkit-visual")
        compileOnlyApi("io.fairyproject:bukkit-timer")
        compileOnlyApi("io.fairyproject:bukkit-nbt")
        compileOnlyApi("io.fairyproject:mc-tablist")
    }

    // Configure ShadowJar task
    tasks.withType(ShadowJar::class) {
        archiveFileName.set("Misty-${properties("version")}.jar")

        // Relocate fairy to avoid plugin conflict
        relocate("net.wesjd.anvilgui", "${properties("group")}.anvilgui")
        relocate("io.fairyproject.bootstrap", "${properties("group")}.fairy.bootstrap")
        relocate("io.fairyproject.bukkit.menu", "${properties("group")}.fairy.menu")
        relocate("io.fairyproject.bukkit.storage", "${properties("group")}.fairy.storage")
        relocate("net.kyori", "io.fairyproject.libs.kyori")
        relocate("com.cryptomorin.xseries", "io.fairyproject.libs.xseries")
        relocate("org.yaml.snakeyaml", "io.fairyproject.libs.snakeyaml")
        relocate("com.google.gson", "io.fairyproject.libs.gson")
        relocate("com.github.retrooper.packetevents", "io.fairyproject.libs.packetevents")
        relocate("io.github.retrooper.packetevents", "io.fairyproject.libs.packetevents")
        archiveClassifier.set("plugin")
        mergeServiceFiles()
        exclude("META-INF/maven/**")
    }
}