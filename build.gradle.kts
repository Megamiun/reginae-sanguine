import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.logging.LogLevel.LIFECYCLE
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    val kotlinVersion = "2.2.0"
    val androidVersion = "8.11.1"

    kotlin("jvm") version kotlinVersion apply false
    kotlin("android") version kotlinVersion apply false
    kotlin("multiplatform") version kotlinVersion apply false
    kotlin("plugin.compose") version kotlinVersion apply false

    id("com.android.application") version androidVersion apply false
    id("com.android.library") version androidVersion apply false

    id("org.jetbrains.compose") version "1.8.2" apply false

    id("org.jlleitschuh.gradle.ktlint") version "13.0.0"
    id("de.undercouch.download") version "5.6.0"

    id("com.dorongold.task-tree") version "4.0.1"
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
        verbose = true
    }
}

tasks {
    val cleanAssets by registering(Delete::class) {
        group = "asset"
        description = "Cleans asset pack for Queen's Blood cards"

        delete(".temp/queens_blood.zip")
        delete(".temp/queens_blood/")
        delete("assets/cards/queens_blood/")
    }

    val downloadAssets by registering(Download::class) {
        group = "asset"
        description = "Downloads asset pack for Queen's Blood cards"

        src("https://www.miguelsanto.com/public/assets/images/projects/queens-blood/queens-blood-package-miguel-espirito-santo.zip")
        dest(".temp/assets/queens_blood.zip")
        overwrite(false)

        doFirst {
            project.logger.log(
                LIFECYCLE,
                "This download may take long, as the zip is 4GB in size. I may create a lightweight version in the future.",
            )
        }
    }

    val unzipAssets by registering(Copy::class) {
        group = "asset"
        description = "Unzip downloaded assets for the game"

        dependsOn(downloadAssets)

        from(zipTree(downloadAssets.get().dest))
        into(".temp/assets/queens_blood/")
    }

    val prepareAssets by registering {
        group = "asset"
        description = "Prepare assets for the game"

        dependsOn(unzipAssets)

        doLast {
            listOf("red", "blue").forEach { color ->
                val uppercaseColor = color.uppercase()
                val assetDir = file("assets/cards/queens_blood/$color")

                fun String.prependToXDigits(digits: Int): String =
                    if (count { char -> char.isDigit() } >= digits) this
                    else "0$this".prependToXDigits(3)

                fileTree(".temp/assets/queens_blood/1. Cards/for Whatever (PNGs)/$uppercaseColor DECK/").forEach {
                    val id = it.name.split(" ")[0].drop(1)
                        .prependToXDigits(3)

                    it.copyTo(assetDir.resolve("$id.png"), true)
                }
            }

            // TODO implement pdf to png or preconvert it
//            file(".temp/assets/queens_blood/2. Board/QB - Board-Final_3MM-BLEED_Standard.pdf")
//                .copyTo(file("assets/board.pdf"), true)
        }
    }
}
