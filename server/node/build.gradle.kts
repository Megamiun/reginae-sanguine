plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("io.kotest") version "6.0.4"
    id("com.google.devtools.ksp") version "2.3.1"
}

kotlin {
    js {
        nodejs()
        binaries.executable()
    }

    sourceSets {
        jsMain {
            resources.srcDirs(rootProject.layout.buildDirectory.dir("generated/resources"))

            dependencies {
                implementation(project(":core"))
                implementation(project(":server:common"))
                implementation(npm("express", "^4.18.2"))
                implementation(npm("pg", "^8.11.3"))
            }
        }

        jsTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.assertions.core)
                implementation(libs.kotlinx.coroutines)
                implementation(npm("node-fetch", "^2.7.0"))
            }
        }
    }
}
