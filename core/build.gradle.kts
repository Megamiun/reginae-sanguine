plugins {
    kotlin("multiplatform") version "2.0.10"
}

val kotestVersion = "5.7.2"

kotlin {
    jvm()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(project.dependencies.platform("io.arrow-kt:arrow-stack:1.2.4"))
            implementation("io.arrow-kt:arrow-core")
        }

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
