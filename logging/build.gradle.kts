import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")

    alias(libs.plugins.kotlin.android.multiplatform.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kover)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(listOf("-Xcontext-parameters", "-Xexpect-actual-classes"))
    }

    jvm()

    androidLibrary {
        namespace = "br.com.gabryel.reginaesanguine.logging"
        compileSdk = 36
        minSdk = 29
    }

    iosArm64()
    iosX64()
    iosSimulatorArm64()

    macosX64()
    macosArm64()
    linuxX64()
    linuxArm64()
    mingwX64()

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
            api(libs.kotlinx.serialization.json)
        }

        appleMain {
            dependsOn(commonMain.get())
        }

        iosMain {
            dependsOn(appleMain.get())
        }

        iosX64Main {
            dependsOn(iosMain.get())
        }

        iosArm64Main {
            dependsOn(iosMain.get())
        }

        iosSimulatorArm64Main {
            dependsOn(iosMain.get())
        }

        macosMain {
            dependsOn(appleMain.get())
        }

        macosX64Main {
            dependsOn(macosMain.get())
        }

        macosArm64Main {
            dependsOn(macosMain.get())
        }

        val fallbackMain by creating {
            dependsOn(commonMain.get())
        }

        wasmJsMain {
            dependsOn(fallbackMain)
        }

        linuxX64Main {
            dependsOn(fallbackMain)
        }

        linuxArm64Main {
            dependsOn(fallbackMain)
        }

        mingwX64Main {
            dependsOn(fallbackMain)
        }
    }
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }
}
