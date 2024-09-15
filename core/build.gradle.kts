plugins {
    kotlin("multiplatform") version "2.0.10"
}

kotlin {
    jvm()

    sourceSets {
        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test"))
        }
    }
}
