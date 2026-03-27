fun rootProperties(key: String) = rootProject.findProperty(key).toString()

group = rootProperties("group")
version = rootProperties("version")

dependencies {
    implementation(project(":plugin:core"))
    implementation(project(":plugin:nms:v1_8_8"))
    implementation(project(":plugin:nms:v1_12_2"))
    implementation(project(":plugin:nms:v1_16_5"))
    implementation(project(":plugin:nms:v1_21"))

    // Spigot dependency
    compileOnly("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT")
}