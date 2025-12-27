fun rootProperties(key: String) = rootProject.findProperty(key).toString()

group = rootProperties("group")
version = rootProperties("version")

// Fairy configuration
fairy {
    name.set("Misty")
    mainPackage.set("me.lotiny.misty.bukkit")
    fairyPackage.set("io.fairyproject")

    // Plugin Dependencies
    bukkitProperties().depends.add("fairy-lib-plugin")

    // Plugin Soft Dependencies
    bukkitProperties().softDepends.add("WorldBorder")
    bukkitProperties().softDepends.add("Chunky")
    bukkitProperties().softDepends.add("ChunkyBorder")
    bukkitProperties().softDepends.add("ViaVersion")
    bukkitProperties().softDepends.add("ViaRewind")
    bukkitProperties().softDepends.add("PlaceholderAPI")
    bukkitProperties().softDepends.add("LuckPerms")
    bukkitProperties().softDepends.add("AquaCoreAPI")
    bukkitProperties().softDepends.add("voicechat")
    bukkitProperties().softDepends.add("Apollo-Bukkit")

    // Plugin Description
    bukkitProperties().authors.add("Lotiny")
    bukkitProperties().website = "https://github.com/Lotiny"
    bukkitProperties().description = "The advanced UHC plugin for Minecraft 1.8.8, 1.12.2, 1.16.5 and 1.21+"
    bukkitProperties().bukkitApi = "1.13"
}

dependencies {
    implementation(project(":plugin:api"))
    implementation(project(":plugin:bukkit"))
    implementation(project(":plugin:common"))
    implementation(project(":plugin:nms:v1_8_8"))
    implementation(project(":plugin:nms:v1_12_2"))
    implementation(project(":plugin:nms:v1_16_5"))
    implementation(project(":plugin:nms:v1_21"))
    implementation(project(":plugin:nms:v1_21_4"))
}