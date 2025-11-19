import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(JVM_11)
        }
    }

    macosX64()
    macosArm64()
    iosArm64()
    iosX64()
    iosSimulatorArm64()

    linuxArm64()
    linuxX64()
    mingwX64()

    js {
        nodejs()
        browser()
        binaries.library()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.library()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":core"))
                implementation(project(":logging"))
                api(libs.kotlinx.serialization.json)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }

        jsMain {
            dependencies {
                implementation(libs.ktor.client.js)
            }
        }

        wasmJsMain {
            dependencies {
                implementation(libs.ktor.client.js)
            }
        }

        appleMain {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }

        iosMain {
            dependsOn(appleMain.get())
        }

        iosArm64Main {
            dependsOn(iosMain.get())
        }

        iosSimulatorArm64Main {
            dependsOn(iosMain.get())
        }

        iosX64Main {
            dependsOn(iosMain.get())
        }

        macosMain {
            dependsOn(appleMain.get())
        }

        macosArm64Main {
            dependsOn(macosMain.get())
        }

        macosX64Main {
            dependsOn(macosMain.get())
        }

        val nativeDesktopMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.ktor.client.curl)
            }
        }

        linuxArm64Main {
            dependsOn(nativeDesktopMain)
        }

        linuxX64Main {
            dependsOn(nativeDesktopMain)
        }

        mingwX64Main {
            dependsOn(nativeDesktopMain)
        }
    }
}
