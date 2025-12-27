fun rootProperties(key: String) = rootProject.findProperty(key).toString()

group = rootProperties("group")
version = rootProperties("version")

dependencies {
    // Spigot dependency
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
}