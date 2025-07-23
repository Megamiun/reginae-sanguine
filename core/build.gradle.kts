import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import kotlin.collections.forEach

plugins {
    kotlin("multiplatform")
}

val kotestVersion = "6.0.0.M4"
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

    listOf(linuxArm64(), linuxX64(), mingwX64()).forEach(::configureNative)

    if (System.getProperty("os.name").startsWith("Mac OS X")) {
        listOf(macosX64(), macosArm64()).forEach(::configureNative)
    }
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }
}

private fun configureNative(target: KotlinNativeTarget) {
    target.binaries.staticLib()
}
