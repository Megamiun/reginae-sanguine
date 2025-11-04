plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvm()
    js {
        nodejs()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("reflect"))
                implementation(project(":core"))
                implementation(kotlin("test"))
                implementation(libs.kotest.assertions.core)

                api(libs.kotlinx.coroutines)
            }
        }
    }
}
