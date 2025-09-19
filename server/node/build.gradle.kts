plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    js {
        nodejs()
    }

    sourceSets {
        commonMain {
            resources.srcDirs(rootProject.layout.buildDirectory.dir("generated/resources"))

            dependencies {
                implementation(kotlin("reflect"))
                implementation(project(":core"))
            }
        }
    }
}
