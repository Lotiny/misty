fun rootProperties(key: String) = rootProject.findProperty(key).toString()

group = rootProperties("group")
version = rootProperties("version")

base {
    archivesName.set("MistyAPI")
}

dependencies {
    // Spigot dependency
    compileOnly("org.spigotmc:spigot-api:26.1.1-R0.1-SNAPSHOT")
}