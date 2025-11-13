rootProject.name = "reginae-sanguine"

pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

include("core", "app:compose", "app:cli", "app:viewmodel", "server:common", "server:spring", "server:node", "playground")
