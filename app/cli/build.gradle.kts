import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")

    alias(libs.plugins.jetbrains.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.arrow.stack))
            implementation(libs.mosaic.runtime)
            implementation(libs.arrow.core)
            implementation(project(":core"))
            implementation(project(":app:viewmodel"))
        }

        all {
            languageSettings.enableLanguageFeature("WhenGuards")
        }
    }

    mutableListOf(linuxArm64(), linuxX64(), mingwX64())
        .forEach(::configureNative)

    if (HostManager.hostIsMac)
        listOf(macosX64(), macosArm64())
            .forEach(::configureNative)

    sourceSets {
        commonMain {
            resources.srcDirs(rootProject.layout.buildDirectory.dir("generated/resources"))
        }
    }
}

fun configureNative(target: KotlinNativeTarget) {
    target.binaries.executable {
        entryPoint = "br.com.gabryel.reginaesanguine.cli.main"
    }
}
