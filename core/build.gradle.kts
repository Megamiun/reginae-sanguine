plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.arrow.stack))
            implementation(libs.arrow.core)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotest.property)
            implementation(libs.kotest.assertions.core)
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
