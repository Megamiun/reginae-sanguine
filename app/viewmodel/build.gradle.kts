plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()

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

    listOf(linuxArm64(), linuxX64(), mingwX64(), iosArm64(), iosX64(), iosSimulatorArm64())

    if (System.getProperty("os.name").startsWith("Mac OS X")) {
        listOf(macosX64(), macosArm64())
    }
}
