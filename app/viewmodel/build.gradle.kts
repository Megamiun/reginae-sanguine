plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")

    alias(libs.plugins.jetbrains.compose)
}

kotlin {
    jvm()
    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.arrow.stack))
            implementation(libs.mosaic.runtime)
            implementation(libs.arrow.core)
            implementation(project(":core"))
        }

        all {
            languageSettings.enableLanguageFeature("WhenGuards")
        }
    }

    listOf(linuxArm64(), linuxX64(), mingwX64())

    if (System.getProperty("os.name").startsWith("Mac OS X")) {
        listOf(macosX64(), macosArm64())
    }
}
