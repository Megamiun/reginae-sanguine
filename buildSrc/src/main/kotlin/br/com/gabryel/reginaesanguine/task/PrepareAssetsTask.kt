package br.com.gabryel.reginaesanguine.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class PrepareAssetsTask : DefaultTask() {
    @get:InputDirectory
    abstract val assetsDir: DirectoryProperty

    @get:OutputDirectory
    abstract val generatedDir: DirectoryProperty

    private val cardDrawablesDir = generatedDir.dir("composeResources/drawable")
    private val fontsDir = generatedDir.dir("composeResources/font")
    private val dynamicResourcesDir = generatedDir.dir("resources")

    @TaskAction
    fun prepareAssets() {
        val basePath = "${assetsDir.asFile.get().path}/"

        assetsDir.asFileTree
            .filter { it.name.endsWith(".png") }
            .forEach { card ->
                val filePath = card.path.removePrefix(basePath)
                val name = filePath.split("/").joinToString("_")
                card.copyTo(cardDrawablesDir.get().file(name.lowercase()).asFile, true)
            }

        assetsDir.asFileTree
            .filter { it.name.endsWith(".otf") || it.name.endsWith(".ttf") }
            .forEach { font ->
                font.copyTo(fontsDir.get().file(font.name).asFile, true)
            }

        assetsDir.asFileTree
            .filter { it.name == "pack_info.json" }
            .forEach { pack ->
                val filePath = pack.path.removePrefix(basePath)
                val name = filePath.split("/").joinToString("/")
                pack.copyTo(dynamicResourcesDir.get().file(name).asFile, true)
            }
    }
}
