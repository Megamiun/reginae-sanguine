import org.gradle.api.JavaVersion.VERSION_11
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")

    id("com.android.application")
    id("org.jetbrains.compose")
}

kotlin {
    androidTarget()
    jvm("desktop") {
        compilerOptions {
            jvmTarget = JVM_11
            freeCompilerArgs = listOf("-XXLanguage:+WhenGuards")
        }
    }

    sourceSets {
        all {
            dependencies {
                implementation(project.dependencies.platform("androidx.compose:compose-bom:2025.07.00"))
            }
        }

        commonMain.dependencies {
            runtimeOnly(compose.runtime)

            implementation(project(":core"))

            implementation(compose.components.resources)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)

            implementation("io.coil-kt.coil3:coil-compose:3.3.0")
        }

        androidMain.dependencies {
            implementation("androidx.appcompat:appcompat:1.7.1")
            implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2")
            implementation("androidx.activity:activity-compose:1.10.1")

            implementation("androidx.compose.ui:ui-graphics")
            implementation("androidx.compose.ui:ui-tooling-preview")
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

    dependencies {
        androidTestImplementation("androidx.compose.ui:ui-test")

        debugImplementation("androidx.compose.ui:ui-tooling")
        debugImplementation("androidx.compose.ui:ui-test-manifest")
    }
}

tasks {
    val prepareAssets by registering {
        group = "asset"
        description = "Prepare assets for the game"

        doLast {
            val drawables = file("src/commonMain/composeResources/drawable")
            drawables.delete()

            rootProject.layout.projectDirectory.dir("assets").asFileTree.forEach { card ->
                val name = card.path.split("/").takeLast(3).joinToString("_")
                card.copyTo(drawables.resolve(name.lowercase()), true)
            }
        }
    }
}
