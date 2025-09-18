import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.kover)
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
            implementation(project.dependencies.platform(libs.arrow.stack))
            implementation(libs.arrow.core)

            api(libs.kotlinx.serialization.json)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))

            implementation(libs.kotest.property)
            implementation(libs.kotest.assertions.core)
        }

        jvmTest.dependencies {
            implementation(libs.mockk)
            implementation(libs.kotest.runner.junit5)
            implementation(libs.kotest.framework.engine)
        }
    }

    sourceSets {
        commonTest {
            resources.srcDirs(rootProject.layout.buildDirectory.dir("generated/resources"))
        }
    }
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }
}
