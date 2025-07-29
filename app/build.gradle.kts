import org.gradle.api.JavaVersion.VERSION_11
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11

plugins {
    kotlin("android")
    kotlin("plugin.compose")

    id("com.android.application")
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

    kotlin {
        compilerOptions {
            jvmTarget = JVM_11
            freeCompilerArgs = listOf("-XXLanguage:+WhenGuards")
        }
    }

    buildFeatures {
        compose = true
    }

    tasks {
        val prepareAssets by registering {
            group = "asset"
            description = "Prepare assets for the game"

            doLast {
                val drawables = file("src/main/res/drawable")
                rootProject.layout.projectDirectory.dir("assets").asFileTree.forEach { card ->
                    val name = card.path.split("/").takeLast(3).joinToString("_")
                    card.copyTo(drawables.resolve(name.lowercase()), true)
                }
            }
        }
    }
}

dependencies {
    implementation(project(":core"))

    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2")
    implementation("androidx.activity:activity-compose:1.10.1")

    implementation(platform("androidx.compose:compose-bom:2025.07.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    androidTestImplementation(platform("androidx.compose:compose-bom:2025.07.00"))
    androidTestImplementation("androidx.compose.ui:ui-test")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
