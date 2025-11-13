import br.com.gabryel.reginaesanguine.task.GenerateLocChartsTask
import br.com.gabryel.reginaesanguine.task.PrepareAssetsTask
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.spring) apply false

    alias(libs.plugins.android.application) apply false

    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false

    alias(libs.plugins.jetbrains.compose) apply false

    alias(libs.plugins.kover) apply false

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
        configureEach {
            if ("Resources" in name)
                dependsOn(rootProject.tasks.getByName("prepareAssets"))
        }

        register("targets") {
            group = "help"

            val projectName = project.name
            val kmpExtension = project.extensions.findByType<KotlinMultiplatformExtension>()
            val sourceSetContainer = project.extensions.findByType<SourceSetContainer>()

            val targetNames = kmpExtension?.targets?.map { it.name } ?: emptyList()

            val kmpSourceSetNames = kmpExtension?.sourceSets?.map { it.name } ?: emptyList()
            val jvmSourceSetNames = sourceSetContainer?.map { it.name } ?: emptyList()
            val sourceSetNames = (kmpSourceSetNames + jvmSourceSetNames).toSet()

            doLast {
                println("Project: $projectName")
                if (targetNames.isNotEmpty()) {
                    println("KMP Targets:")
                    targetNames.forEach { println(" - $it") }
                }
                if (sourceSetNames.isNotEmpty()) {
                    println("Source Sets:")
                    sourceSetNames.forEach { println(" - $it") }
                }
                if (targetNames.isEmpty() && sourceSetNames.isEmpty()) {
                    println("No targets or source sets found")
                }
            }
        }
    }
}

tasks {
    register("prepareAssets", PrepareAssetsTask::class) {
        group = "assets"

        assetsDir = rootProject.layout.projectDirectory.dir("assets")
        generatedDir = rootProject.layout.buildDirectory.dir("generated")
    }

    register("generateLocCharts", GenerateLocChartsTask::class) {
        group = "documentation"
        description = "Generate LOC charts for all modules"

        projectDir = rootProject.layout.projectDirectory
        outputDir = rootProject.layout.buildDirectory.dir("charts")
    }
}
