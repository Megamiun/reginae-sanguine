plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kotlin.serialization)

    id("org.jetbrains.kotlinx.kover") version "0.9.1"
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.arrow.stack))
            implementation(libs.arrow.core)

            api(libs.kotlinx.serialization.json)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))

            implementation(libs.kotest.property)
            implementation(libs.kotest.assertions.core)
        }

        jvmTest.dependencies {
            implementation(libs.mockk)
            implementation(libs.kotest.runner.junit5)
            implementation(libs.kotest.framework.engine)
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
