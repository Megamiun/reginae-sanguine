import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    val kotlinVersion = "2.2.0"
    val androidVersion = "8.11.1"

    kotlin("android") version kotlinVersion apply false
    kotlin("multiplatform") version kotlinVersion apply false
    kotlin("plugin.compose") version kotlinVersion apply false

    id("com.android.application") version androidVersion apply false
    id("com.android.library") version androidVersion apply false

    id("org.jetbrains.compose") version "1.8.2" apply false

    id("org.jlleitschuh.gradle.ktlint") version "13.0.0"
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    group = "br.com.gabryel"
    version = "0.1-SNAPSHOT"

    repositories {
        mavenCentral()
        google()
    }

    configure<KtlintExtension> {
        verbose = true
    }
}
