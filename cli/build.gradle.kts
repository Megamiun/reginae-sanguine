import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
}

val arrowVersion = "2.1.2"

kotlin {
    sourceSets {
        nativeMain.dependencies {
            implementation(project.dependencies.platform("io.arrow-kt:arrow-stack:$arrowVersion"))
            implementation("io.arrow-kt:arrow-core")
            implementation(project(":core"))
        }
    }

    listOf(linuxArm64(), linuxX64(), mingwX64()).forEach(::configureNative)

    if (System.getProperty("os.name").startsWith("Mac OS X")) {
        listOf(macosX64(), macosArm64()).forEach(::configureNative)
    }
}

fun configureNative(target: KotlinNativeTarget) {
    target.binaries.executable {
        entryPoint = "br.com.gabryel.reginaesanguine.cli.main"
    }
}
