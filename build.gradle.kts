import br.com.gabryel.reginaesanguine.task.PrepareAssetsTask
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.spring) apply false

    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false

    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false

    alias(libs.plugins.jetbrains.compose) apply false

    alias(libs.plugins.ktlint)
    alias(libs.plugins.download)

    alias(libs.plugins.task.tree)
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    group = "br.com.gabryel"
    version = "0.1-SNAPSHOT"

    repositories {
        mavenCentral()
        google()
    }

    configure<KtlintExtension> {
        version = "1.7.1"
        verbose = true
    }

    tasks {
        whenTaskAdded {
            if ("Process.*Resources".toRegex() in javaClass.simpleName)
                dependsOn(rootProject.tasks.getByName("prepareAssets"))
        }
    }
}

tasks {
    register("prepareAssets", PrepareAssetsTask::class) {
        group = "assets"

        assetsDir = rootProject.layout.projectDirectory.dir("assets")
        generatedDir = rootProject.layout.buildDirectory.dir("generated")
    }
}
