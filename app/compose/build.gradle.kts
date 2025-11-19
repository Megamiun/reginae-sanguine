import org.gradle.api.JavaVersion.VERSION_11
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")

    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
}

val generatedResources = rootProject.layout.buildDirectory.dir("generated/resources")

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            listOf(
                "-Xcontext-parameters",
                "-Xexpect-actual-classes",
                "-XXLanguage:+WhenGuards",
            ),
        )
    }

    androidTarget()
    jvm {
        mainRun {
            mainClass = "br.com.gabryel.reginaesanguine.app.JvmMainKt"
        }
        compilerOptions {
            jvmTarget = JVM_11
        }
    }

    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ReginaeSanguineCompose"
            isStatic = true
        }
    }

    js {
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "reginae-sanguine-app-compose.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static(rootDirPath)
                    static(projectDirPath)
                }

                watchOptions = KotlinWebpackConfig.WatchOptions(ignored = listOf("**/node_modules"))
            }
        }

        binaries.executable()
    }

    sourceSets {
        val nonAndroidMain by creating {
            kotlin.srcDir("src/nonAndroidMain/kotlin")

            dependsOn(commonMain.get())

            dependencies {
                implementation(libs.coil.compose)
                implementation(compose.ui)
            }
        }

        all {
            if ("android" !in name && "common" !in name && "Main" in name) {
                dependsOn(nonAndroidMain)
            }

            dependencies {
                implementation(project(":core"))
                implementation(project(":logging"))
                implementation(project(":app:viewmodel"))
                implementation(project(":server:dto"))

                runtimeOnly(compose.runtime)

                implementation(compose.materialIconsExtended)
                implementation(compose.components.resources)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)

                implementation(libs.coil.compose)
                runtimeOnly(libs.coil.network.ktor)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
            }
        }

        androidMain.dependencies {
            implementation(libs.appcompat)
            implementation(libs.lifecycle.runtime.ktx)
            implementation(libs.activity.compose)

            implementation(compose.preview)
            implementation(libs.ktor.client.okhttp)
        }

        jvmMain.dependencies {
            runtimeOnly(compose.desktop.currentOs)
            runtimeOnly(compose.desktop.common)
            runtimeOnly(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        jsMain.dependencies {
            implementation(libs.ktor.client.js)
        }

        commonMain {
            resources.srcDir(generatedResources)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        jvmTest.dependencies {
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
            implementation(libs.mockk)
        }

        iosMain {
            dependsOn(nonAndroidMain)
            dependsOn(commonMain.get())
        }

        iosX64Main {
            dependsOn(iosMain.get())
        }

        iosArm64Main {
            dependsOn(iosMain.get())
        }

        iosSimulatorArm64Main {
            dependsOn(iosMain.get())
        }
    }
}

compose {
    resources {
        publicResClass = true
        packageOfResClass = "br.com.gabryel.reginaesanguine.app"
        generateResClass = always

        customDirectory(
            "commonMain",
            rootProject.layout.buildDirectory.dir("generated/composeResources"),
        )
    }

    desktop {
        application {
            mainClass = "br.com.gabryel.reginaesanguine.app.JvmMainKt"

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
}

android {
    namespace = "br.com.gabryel.reginaesanguine"
    compileSdk = 36

    defaultConfig {
        applicationId = "br.com.gabryel.reginaesanguine"
        minSdk = 29
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
            assets.srcDir(generatedResources)
        }
    }

    dependencies {
        debugImplementation(compose.uiTooling)
    }
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }
}
