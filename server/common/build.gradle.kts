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
                api(project(":core"))
                api(project(":server:dto"))

                implementation(kotlin("reflect"))
                implementation(kotlin("test"))
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.assertions.core)

                api(libs.kotlinx.coroutines)
                api(libs.kotlinx.datetime)
            }
        }
    }

    tasks.withType<Test> {
        enabled = false
    }
}
