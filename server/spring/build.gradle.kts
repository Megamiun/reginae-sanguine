plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("plugin.spring")

    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation(kotlin("reflect"))

    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.web)

    implementation(project(":core"))

    testImplementation(libs.spring.boot.starter.test)
}

springBoot {
    mainClass = "br.com.reginaesanguine.server.ServerKt"
}
