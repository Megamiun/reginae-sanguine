plugins {
    kotlin("multiplatform") version "2.0.10"
}

val kotestVersion = "5.7.2"

kotlin {
    jvm()

    sourceSets {
        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test"))
            implementation("io.kotest:kotest-property:$kotestVersion")
            implementation("io.kotest:kotest-assertions-core:$kotestVersion")
        }
    }
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }
}