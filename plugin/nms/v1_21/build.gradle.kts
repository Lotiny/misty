fun rootProperties(key: String) = rootProject.findProperty(key).toString()

group = rootProperties("group")
version = rootProperties("version")

dependencies {
    implementation(project(":plugin:common"))

    // Spigot dependency
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
}