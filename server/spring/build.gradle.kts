plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("plugin.spring")
    kotlin("plugin.jpa") version libs.versions.kotlin.get()

    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

repositories {
    maven(url = "https://repo.spring.io/artifactory/snapshot")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation(project(":core"))
    implementation(project(":server:common"))

    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.flyway)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.flyway.core)

    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.flyway.database.postgresql)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.restclient)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotlinx.coroutines)
    testImplementation(libs.kotest.extensions.spring)
    testImplementation(libs.kotest.extensions.testcontainers)

    testImplementation(libs.testcontainers.postgresql)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

sourceSets {
    main {
        resources.srcDirs(
            rootProject.layout.buildDirectory.dir("generated/resources"),
            project(":server:common").projectDir.resolve("src/commonMain/resources"),
        )
    }
}

springBoot {
    mainClass = "br.com.gabryel.reginaesanguine.server.ServerKt"
}
