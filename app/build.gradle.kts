import org.gradle.api.JavaVersion.VERSION_11
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")

    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
}

kotlin {
    androidTarget()
    jvm("desktop") {
        compilerOptions {
            jvmTarget = JVM_11
            freeCompilerArgs = listOf("-XXLanguage:+WhenGuards")
        }
    }

    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries {
            framework {
                baseName = "shared"
                isStatic = true
            }
        }
    }

    sourceSets {
        all {
            dependencies {
                implementation(project.dependencies.platform(libs.compose.bom))
            }
        }

        commonMain.dependencies {
            runtimeOnly(compose.runtime)

            implementation(project(":core"))

            implementation(compose.components.resources)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)

            implementation(libs.coil.compose)
        }

        androidMain.dependencies {
            implementation(libs.appcompat)
            implementation(libs.lifecycle.runtime.ktx)
            implementation(libs.activity.compose)

            implementation(libs.compose.ui.graphics)
            implementation(libs.compose.ui.tooling.preview)
        }

        get("desktopMain").dependencies {
            runtimeOnly(compose.desktop.currentOs)
            implementation(compose.desktop.common)
        }
    }
}

compose.desktop {
    application {
        mainClass = "br.com.gabryel.reginaesanguine.MainKt"

        nativeDistributions {
            targetFormats(Dmg, Msi, Deb)
            packageName = "Reginae Sanguine"
            packageVersion = "1.0.0"

            windows {
                menu = true
                upgradeUuid = "7291a285-2f28-4558-ae9e-90f421747bdc"
            }
        }
    }
}

android {
    namespace = "br.com.gabryel.reginaesanguine"
    compileSdk = 36

    defaultConfig {
        applicationId = "br.com.gabryel.reginaesanguine"
        minSdk = 28
    }

    compileOptions {
        sourceCompatibility = VERSION_11
        targetCompatibility = VERSION_11
    }

    buildFeatures {
        compose = true
    }

    sourceSets {
        getByName("main") {
            assets {
                srcDirs("src/androidMain/assets", "src/commonMain/resources")
            }
        }
    }

    dependencies {
        androidTestImplementation(libs.compose.ui.test)

        debugImplementation(libs.compose.ui.tooling)
        debugImplementation(libs.compose.ui.test.manifest)
    }
}

tasks {
    register("prepareAssets") {
        group = "asset"
        description = "Prepare assets for the game"

        doLast {
            val drawables = file("src/commonMain/composeResources/drawable")
            drawables.delete()

            rootProject.layout.projectDirectory.dir("assets").asFileTree
                .filter { it.name.endsWith(".png") }
                .forEach { card ->
                    val name = card.path.split("/").takeLast(3).joinToString("_")
                    card.copyTo(drawables.resolve(name.lowercase()), true)
                }
        }
    }
}
