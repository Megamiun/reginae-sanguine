import org.gradle.api.JavaVersion.VERSION_11
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")

    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
}

val generatedResources = rootProject.layout.buildDirectory.dir("generated/resources")

kotlin {
    compilerOptions {
        freeCompilerArgs = listOf("-Xcontext-parameters", "-Xexpect-actual-classes")
    }

    androidTarget()
    jvm("desktop") {
        mainRun {
            mainClass = "br.com.gabryel.reginaesanguine.app.MainKt"
        }
        compilerOptions {
            jvmTarget = JVM_11
            freeCompilerArgs = listOf("-XXLanguage:+WhenGuards")
        }
    }

    if (System.getProperty("os.name").startsWith("Mac OS X")) {
        listOf(iosX64(), iosArm64(), iosSimulatorArm64())
    }

    sourceSets {
        all {
            dependencies {
                implementation(project.dependencies.platform(libs.compose.bom))
            }
        }

        commonMain.dependencies {
            implementation(project(":core"))
            implementation(project(":app:viewmodel"))
            runtimeOnly(compose.runtime)

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

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.add("-Xcontext-parameters")
        }
    }

    named("copyNonXmlValueResourcesForCommonMain") {
        dependsOn(rootProject.tasks.named("prepareAssets"))
    }
}
