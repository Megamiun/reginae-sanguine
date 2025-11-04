plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    js {
        nodejs()
        binaries.executable()
    }

    sourceSets {
        jsMain {
            dependencies {
                implementation(project(":core"))
                implementation(project(":server:common"))
                implementation(npm("express", "^4.18.2"))
            }
        }

        jsTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines)
                implementation(npm("node-fetch", "^2.7.0"))
            }
        }
    }
}
