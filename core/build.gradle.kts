plugins {
    kotlin("multiplatform")
}

val kotestVersion = "6.0.0.M8"
val arrowVersion = "2.1.2"

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform("io.arrow-kt:arrow-stack:$arrowVersion"))
            implementation("io.arrow-kt:arrow-core")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("io.kotest:kotest-property:$kotestVersion")
            implementation("io.kotest:kotest-assertions-core:$kotestVersion")
        }
    }

    listOf(linuxArm64(), linuxX64(), mingwX64())

    if (System.getProperty("os.name").startsWith("Mac OS X")) {
        listOf(macosX64(), macosArm64())
    }
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }
}
