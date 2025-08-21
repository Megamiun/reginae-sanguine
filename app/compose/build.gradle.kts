import org.gradle.api.JavaVersion.VERSION_11
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
        freeCompilerArgs.addAll(listOf("-Xcontext-parameters", "-Xexpect-actual-classes", "-XXLanguage:+WhenGuards"))
    }

    androidTarget()
    jvm {
        mainRun {
            mainClass = "br.com.gabryel.reginaesanguine.app.MainKt"
        }
        compilerOptions {
            jvmTarget = JVM_11
        }
    }

    iosArm64()
    iosX64()
    iosSimulatorArm64()

    js {
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "reginae-sanguine-app-compose.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }

                watchOptions = KotlinWebpackConfig.WatchOptions(ignored = listOf("**/node_modules"))
            }
        }

        binaries.executable()
    }

    sourceSets {
        all {
            dependencies {
                implementation(project(":core"))
                implementation(project(":app:viewmodel"))

                implementation(project.dependencies.platform(libs.compose.bom))
                runtimeOnly(compose.runtime)

                implementation(compose.components.resources)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)

                implementation("io.coil-kt.coil3:coil-compose:3.3.0")
                runtimeOnly("io.coil-kt.coil3:coil-network-ktor:3.0.0-alpha08")
            }
        }

        androidMain.dependencies {
            implementation(libs.appcompat)
            implementation(libs.lifecycle.runtime.ktx)
            implementation(libs.activity.compose)

            runtimeOnly(libs.compose.ui.graphics)
            runtimeOnly(libs.compose.ui.tooling.preview)
        }

        jvmMain.dependencies {
            runtimeOnly(compose.desktop.currentOs)
            runtimeOnly(compose.desktop.common)
            runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-swing")
        }

        commonMain {
            resources.srcDir(generatedResources)
        }
    }
}

compose {
    resources {
        publicResClass = true
        packageOfResClass = "br.com.gabryel.reginaesanguine.app"
        generateResClass = always

        customDirectory("commonMain", rootProject.layout.buildDirectory.dir("generated/composeResources"))
    }

    desktop {
        application {
            mainClass = "br.com.gabryel.reginaesanguine.app.MainKt"

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
            assets.srcDir(generatedResources)
        }
    }

    dependencies {
        androidTestImplementation(libs.compose.ui.test)

        debugImplementation(libs.compose.ui.tooling)
        debugImplementation(libs.compose.ui.test.manifest)
    }
}
