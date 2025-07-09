plugins {
    kotlin("multiplatform")
}

val kotestVersion = "6.0.0.M4"
val arrowVersion = "2.1.2"

kotlin {
    jvm()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(project.dependencies.platform("io.arrow-kt:arrow-stack:$arrowVersion"))
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
