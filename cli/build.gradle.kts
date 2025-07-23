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

    listOf(linuxArm64(), linuxX64(), mingwX64()).forEach { nativeTarget ->
        nativeTarget.binaries.executable {
            entryPoint = "br.com.gabryel.reginaesanguine.cli.main"
        }
    }
}
