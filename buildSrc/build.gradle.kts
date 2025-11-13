plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-jvm:4.9.3")
    implementation("org.jetbrains.lets-plot:lets-plot-image-export:4.5.1")
}
