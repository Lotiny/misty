fun rootProperties(key: String) = rootProject.findProperty(key).toString()

group = rootProperties("group")
version = rootProperties("version")

dependencies {
    implementation(project(":plugin:common"))
    implementation(project(":plugin:nms:v1_21_4"))

    // Spigot dependency
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
}