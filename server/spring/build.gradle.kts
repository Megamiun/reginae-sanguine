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
    implementation(kotlin("reflect"))

    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.web) {
        exclude(module = "org.springframework:spring-webmvc") // TODO Remove on Spring Boot 7.0.0-M9
    }

    implementation(libs.spring.webmvc) // TODO Remove on Spring Boot 7.0.0-M9

    implementation(project(":core"))

    testImplementation(libs.spring.boot.starter.test)
}

sourceSets {
    main {
        val generatedResources = rootProject.layout.buildDirectory.dir("generated/resources")
        resources.srcDirs(generatedResources, "src/main/resources")
    }
}

springBoot {
    mainClass = "br.com.gabryel.reginaesanguine.server.ServerKt"
}
