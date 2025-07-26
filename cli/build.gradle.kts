import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

val arrowVersion = "2.1.2"
val mosaicVersion = "0.17.0"

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform("io.arrow-kt:arrow-stack:$arrowVersion"))
            implementation("com.jakewharton.mosaic:mosaic-runtime:$mosaicVersion")
            implementation("io.arrow-kt:arrow-core")
            implementation(project(":core"))
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
