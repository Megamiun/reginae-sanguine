import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11

plugins {
    kotlin("multiplatform") version "2.2.0"
    kotlin("plugin.compose") version "2.2.0"

    alias(libs.plugins.jetbrains.compose)
}

repositories {
    mavenCentral()
    google()
}

kotlin {
    jvm("desktop") {
        mainRun {
            mainClass = "MainKt"
        }
        compilerOptions {
            jvmTarget = JVM_11
            freeCompilerArgs = listOf("-XXLanguage:+WhenGuards")
        }
    }

    sourceSets {
        get("desktopMain").dependencies {
            implementation(project.dependencies.platform(libs.compose.bom))

            runtimeOnly(compose.runtime)

            implementation(compose.components.resources)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)

            runtimeOnly(compose.desktop.currentOs)
            implementation(compose.desktop.common)
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(Dmg, Msi, Deb)
            packageName = "ScaffoldError"
            packageVersion = "1.0.0"

            windows {
                menu = true
                upgradeUuid = "7291a285-2f28-4558-ae9e-90f421747bdc"
            }
        }
    }
}
