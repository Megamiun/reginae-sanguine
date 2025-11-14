import org.gradle.api.JavaVersion.VERSION_11
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.konan.target.HostManager

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

    if (HostManager.hostIsMac) {
        val iosTargets = listOf(iosX64(), iosArm64(), iosSimulatorArm64())
        iosTargets.forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = "ReginaeSanguineCompose"
                isStatic = true
            }
        }
    }

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
        val nonAndroidMain by creating {
            kotlin.srcDir("src/nonAndroidMain/kotlin")

            dependsOn(commonMain.get())

            dependencies {
                implementation(libs.coil.compose)
                implementation(compose.ui)
            }
        }

        if (HostManager.hostIsMac) {
            val iosMain by creating {
                dependsOn(commonMain.get())
                dependsOn(nonAndroidMain)
            }

            val iosX64Main by getting { dependsOn(iosMain) }
            val iosArm64Main by getting { dependsOn(iosMain) }
            val iosSimulatorArm64Main by getting { dependsOn(iosMain) }
        }

        all {
            if ("android" !in name && "common" !in name && "Main" in name) {
                dependsOn(nonAndroidMain)
            }

            dependencies {
                implementation(project(":core"))
                implementation(project(":app:viewmodel"))

                runtimeOnly(compose.runtime)

                implementation(compose.components.resources)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)

                implementation(libs.coil.compose)
                runtimeOnly(libs.coil.network.ktor)
            }
        }

        androidMain.dependencies {
            implementation(libs.appcompat)
            implementation(libs.lifecycle.runtime.ktx)
            implementation(libs.activity.compose)

            implementation(compose.preview)
        }

        jvmMain.dependencies {
            runtimeOnly(compose.desktop.currentOs)
            runtimeOnly(compose.desktop.common)
            runtimeOnly(libs.kotlinx.coroutines.swing)
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
