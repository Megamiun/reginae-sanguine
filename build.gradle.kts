plugins {
    kotlin("multiplatform") version "2.2.0" apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    group = "br.com.gabryel"
    version = "0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}
