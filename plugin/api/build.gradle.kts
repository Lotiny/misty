fun rootProperties(key: String) = rootProject.findProperty(key).toString()

group = rootProperties("group")
version = rootProperties("version")

base {
    archivesName.set("MistyAPI")
}

dependencies {
    // Spigot dependency
    compileOnly("org.spigotmc:spigot-api:1.21.11-R0.1-SNAPSHOT")
}