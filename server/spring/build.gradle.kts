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
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.web) {
        exclude(module = "org.springframework:spring-web")
    }

    implementation(libs.spring.web)
}

springBoot {
    mainClass = "br.com.reginaesanguine.server.ServerKt"
}
