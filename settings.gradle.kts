pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://repo.imanity.dev/imanity-libraries")
    }
}

rootProject.name = "Misty"
include(":plugin:api")
include(":plugin:core")
include(":plugin:paper")
include(":plugin:nms:v1_8_8")
include(":plugin:nms:v1_12_2")
include(":plugin:nms:v1_16_5")
include(":plugin:nms:v1_21")
include(":plugin:nms:v1_21_4")
include(":plugin:nms:v1_21_11")
include(":plugin:dist")
