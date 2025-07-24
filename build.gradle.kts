import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    kotlin("multiplatform") version "2.2.0" apply false
    kotlin("plugin.compose") version "2.2.0" apply false
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
