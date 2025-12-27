fun rootProperties(key: String) = rootProject.findProperty(key).toString()

group = rootProperties("group")
version = rootProperties("version")

dependencies {
    implementation(project(":plugin:common"))

    // Spigot dependency
    compileOnly(files("${rootProject.projectDir}/libs/patched_1.8.8.jar"))
    compileOnly(files("${rootProject.projectDir}/libs/mojang_1.8.8.jar"))
}