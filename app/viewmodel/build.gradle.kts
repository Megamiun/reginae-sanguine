import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    linuxArm64()
    linuxX64()
    mingwX64()

    if (HostManager.hostIsMac) {
        macosX64()
        macosArm64()
        iosArm64()
        iosX64()
        iosSimulatorArm64()
    }

    js {
        browser()
        binaries.library()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.library()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))

            implementation(project.dependencies.platform(libs.arrow.stack))
            implementation(libs.arrow.core)

            implementation(libs.kotlinx.coroutines)
        }

        all {
            languageSettings.enableLanguageFeature("WhenGuards")
        }
    }
}
