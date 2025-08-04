import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")

    alias(libs.plugins.jetbrains.compose)
}

kotlin {
    compilerOptions {
        freeCompilerArgs = listOf("-Xcontext-parameters", "-Xexpect-actual-classes")
    }

    jvm("desktop") {
        mainRun {
            mainClass = "br.com.gabryel.reginaesanguine.app.MainKt"
        }
        compilerOptions {
            jvmTarget = JVM_11
            freeCompilerArgs = listOf("-Xcontext-parameters", "-XXLanguage:+WhenGuards")
        }
    }

    sourceSets {
        all {
            dependencies {
                implementation(project.dependencies.platform(libs.compose.bom))
            }
        }

        commonMain.dependencies {
            runtimeOnly(compose.runtime)

            implementation(compose.components.resources)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)

            implementation(libs.coil.compose)
        }

        get("desktopMain").dependencies {
            runtimeOnly(compose.desktop.currentOs)
            implementation(compose.desktop.common)
        }
    }
}

compose.desktop {
    application {
        mainClass = "br.com.gabryel.reginaesanguine.app.MainKt"

        nativeDistributions {
            targetFormats(Dmg, Msi, Deb)
            packageName = "Reginae Sanguine"
            packageVersion = "1.0.0"

            windows {
                menu = true
                upgradeUuid = "7291a285-2f28-4558-ae9e-90f421747bdc"
            }
        }
    }
}
