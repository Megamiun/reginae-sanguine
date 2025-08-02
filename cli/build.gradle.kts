import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

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
            implementation(project(":viewmodel"))
        }

        all {
            languageSettings.enableLanguageFeature("WhenGuards")
        }
    }

    listOf(linuxArm64(), linuxX64(), mingwX64()).forEach(::configureNative)

    if (System.getProperty("os.name").startsWith("Mac OS X")) {
        listOf(macosX64(), macosArm64()).forEach(::configureNative)
    }
}

fun configureNative(target: KotlinNativeTarget) {
    target.binaries.executable {
        entryPoint = "br.com.gabryel.reginaesanguine.cli.main"
    }
}
