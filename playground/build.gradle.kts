import org.gradle.api.JavaVersion.VERSION_11

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.android.application)
}

kotlin {
    jvm()
    androidTarget()
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
}
