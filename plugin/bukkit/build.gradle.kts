fun rootProperties(key: String) = rootProject.findProperty(key).toString()

group = rootProperties("group")
version = rootProperties("version")

// Repositories
repositories {
    maven(url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/"))
    maven(url = uri("https://repo.codemc.io/repository/maven-snapshots/"))
    maven(url = uri("https://maven.maxhenkel.de/repository/public"))
    maven(url = uri("https://repo.lunarclient.dev"))
    maven(url = uri("https://libraries.minecraft.net/"))
}

// Dependencies
dependencies {
    implementation(project(":plugin:api"))
    implementation(project(":plugin:common"))
    implementation(project(":plugin:nms:v1_8_8"))
    implementation(project(":plugin:nms:v1_12_2"))
    implementation(project(":plugin:nms:v1_16_5"))
    implementation(project(":plugin:nms:v1_21"))
    implementation(project(":plugin:nms:v1_21_4"))
    implementation(project(":plugin:nms:v1_21_11"))

    // Spigot dependency
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    // WorldBorder dependency
    compileOnly(files("${rootProject.projectDir}/libs/WorldBorder.jar"))
    // AquaCore dependency
    compileOnly(files("${rootProject.projectDir}/libs/core/AquaCoreAPI.jar"))
    // Simple Voice Chat dependency
    compileOnly("de.maxhenkel.voicechat:voicechat-api:2.5.36")
    // Chunky dependency
    compileOnly("org.popcraft:chunky-common:1.3.38")
    // LuckPerms dependency
    compileOnly("net.luckperms:api:5.4")
    // PlaceholderAPI dependency
    compileOnly("me.clip:placeholderapi:2.11.6")
    // MongoDB dependency
    compileOnly("org.mongodb:mongodb-driver-sync:5.5.1")
    // HikariCP dependency
    compileOnly("com.zaxxer:HikariCP:7.0.2")
    // Apollo dependency
    compileOnly("com.lunarclient:apollo-api:1.2.1")
    // SLF4J dependency
    compileOnly("org.slf4j:slf4j-api:2.0.17")
    compileOnly("org.slf4j:slf4j-jdk14:2.0.17")
    // Mojang Auth
    compileOnly("com.mojang:authlib:6.0.54")
    // Anvil GUI
    implementation("net.wesjd:anvilgui:1.10.10-SNAPSHOT")
    // ConfigLib
    implementation("de.exlll:configlib-yaml:4.6.4")
    implementation("de.exlll:configlib-paper:4.6.4")
}