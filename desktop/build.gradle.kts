import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")

    id("org.jetbrains.compose")
}

kotlin {
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            runtimeOnly(compose.runtime)

            implementation(project(":core"))

            implementation(compose.components.resources)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)

            implementation("io.coil-kt.coil3:coil-compose:3.3.0")
        }

        get("desktopMain").dependencies {
            runtimeOnly(compose.desktop.currentOs)
            implementation(compose.desktop.common)
        }
    }
}

compose.desktop {
    application {
        mainClass = "br.com.gabryel.reginaesanguine.MainKt"

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
