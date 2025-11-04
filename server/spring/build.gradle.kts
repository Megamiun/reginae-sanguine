plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("plugin.spring")

    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

repositories {
    maven(url = "https://repo.spring.io/artifactory/snapshot")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.web)

    implementation(project(":core"))
    implementation(project(":server:common"))

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.restclient)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotlinx.coroutines)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

sourceSets {
    main {
        resources.srcDirs(rootProject.layout.buildDirectory.dir("generated/resources"))
    }
}

springBoot {
    mainClass = "br.com.gabryel.reginaesanguine.server.ServerKt"
}
