package br.com.gabryel.reginaesanguine.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jetbrains.letsPlot.Stat
import org.jetbrains.letsPlot.export.ggsave
import org.jetbrains.letsPlot.geom.geomBar
import org.jetbrains.letsPlot.geom.geomText
import org.jetbrains.letsPlot.ggplot
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.label.labs
import org.jetbrains.letsPlot.pos.positionDodge
import org.jetbrains.letsPlot.scale.scaleYLog10
import java.io.File
import kotlin.math.max
import kotlin.math.roundToInt

data class TargetLoc(
    val module: String,
    val target: String,
    val loc: Int,
    val type: String
)

data class ChartData(
    val targets: List<String>,
    val locs: List<Int>,
    val labels: List<String>
)

abstract class GenerateLocChartsTask : DefaultTask() {
    @get:InputDirectory
    abstract val projectDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    private val modules = listOf(
        "core",
        "app/cli",
        "app/compose",
        "app/viewmodel",
        "server/common",
        "server/spring",
        "server/node",
        "buildSrc",
        "playground",
    )

    private val targetTypeMapping = mapOf(
        "commonMain" to "common",
        "jvmMain" to "jvm",
        "jsMain" to "js",
        "androidMain" to "android",
        "iosMain" to "ios",
        "nonAndroidMain" to "nonAndroid",
        "main" to "jvm",
    )

    @TaskAction
    fun generateCharts() {
        val projectRoot = projectDir.asFile.get()
        outputDir.asFile.get().mkdirs()

        logger.lifecycle("Counting lines of code...")
        val locData = countLoc(projectRoot)

        printSummary(locData)

        logger.lifecycle("\nGenerating charts in: ${outputDir.asFile.get().absolutePath}")
        generateAllCharts(locData)

        logger.lifecycle("\nDone! Charts saved to: ${outputDir.asFile.get().absolutePath}")
    }

    private fun countLoc(projectRoot: File): List<TargetLoc> {
        val results = modules.flatMap { module ->
            val srcDir = File(projectRoot, "$module/src")
            if (!srcDir.exists() || !srcDir.isDirectory)
                return@flatMap emptyList()

            srcDir.listFiles()?.mapNotNull { targetDir ->
                if (!targetDir.isDirectory) return@mapNotNull null

                val targetType = targetTypeMapping[targetDir.name] ?: return@mapNotNull null

                val ktFiles = targetDir.walkTopDown()
                    .filter { it.extension == "kt" }
                    .toList()

                if (ktFiles.isEmpty()) return@mapNotNull null

                val totalLines = ktFiles.sumOf { it.readLines().size }
                TargetLoc(module, targetType, totalLines, "production")
            }.orEmpty()
        }

        val buildFiles = projectRoot.walkTopDown()
            .filter { file ->
                (
                    file.name == "build.gradle.kts" ||
                        file.name == "settings.gradle.kts" ||
                        file.name == "gradle.properties"
                ) &&
                    !file.path.contains("/build/") &&
                    !file.path.contains("/node_modules/")
            }
            .map { file ->
                val relativePath = file.relativeTo(projectRoot).path
                val loc = file.readLines().size
                val category = when {
                    file.name.endsWith(".gradle.kts") -> "build.gradle.kts"
                    file.name == "gradle.properties" -> "gradle.properties"
                    else -> "others"
                }
                TargetLoc(category, relativePath, loc, "build")
            }
            .toList()

        val taskFiles = File(projectRoot, "buildSrc/src/main/kotlin")
            .walkTopDown()
            .filter { it.extension == "kt" }
            .map { file ->
                val relativePath = file.relativeTo(projectRoot).path
                val loc = file.readLines().size
                TargetLoc("*Task.kt", relativePath, loc, "build")
            }
            .toList()

        return results + buildFiles + taskFiles
    }

    private fun generateAllCharts(locData: List<TargetLoc>) {
        val productionData = locData.filter { it.type == "production" }
        val buildData = locData.filter { it.type == "build" }

        // Generate individual module charts
        productionData.groupBy { it.module }
            .filter { (_, data) -> data.isNotEmpty() }
            .forEach { (module, data) ->
                generateSimpleChart(module, data, "#4682B4", "Lines of Code - $module")
            }

        if (buildData.isNotEmpty())
            generateBuildFilesCategoryChart(buildData)

        // Generate aggregate chart for all production code
        generateSimpleChart(
            "aggregate_all_modules",
            productionData,
            "#006400",
            "Total Lines of Code - All Modules (Production)",
        )

        // Generate aggregate charts by module type
        productionData.groupBy { getModuleType(it.module) }
            .filter { (moduleType, moduleData) -> moduleType != null && moduleData.isNotEmpty() }
            .mapKeys { (moduleType) -> moduleType!! }
            .forEach { (moduleType, moduleData) ->
                val color = getModuleTypeColor(moduleType)
                val fileName = "aggregate_${moduleType.lowercase()}"
                generateSimpleChart(fileName, moduleData, color, "Lines of Code - $moduleType Modules")
            }

        // Generate comparison chart
        generateComparisonChart(locData)
    }

    private fun prepareChartData(data: List<TargetLoc>): ChartData? {
        val aggregated = data.groupBy { it.target }
            .mapValues { it.value.sumOf { loc -> loc.loc } }
            .toList()
            .sortedByDescending { it.second }

        if (aggregated.isEmpty()) return null

        val totalLoc = aggregated.sumOf { it.second }
        val targets = aggregated.map { it.first }
        val locs = aggregated.map { max(it.second, 1) }
        val labels = aggregated.map { (_, loc) ->
            val percentage = (loc.toDouble() / totalLoc * 100).roundToInt()
            "$loc\n($percentage%)"
        }

        return ChartData(targets, locs, labels)
    }

    private fun generateSimpleChart(
        fileName: String,
        data: List<TargetLoc>,
        color: String,
        title: String,
        xAxisLabel: String = "Target Type"
    ) {
        val chartData = prepareChartData(data) ?: return

        val dataMap = mapOf(
            "target" to chartData.targets,
            "loc" to chartData.locs,
            "label" to chartData.labels,
        )

        val plot = ggplot(dataMap) {
            x = "target"
            y = "loc"
        } +
            geomBar(stat = Stat.identity, color = color, fill = color, alpha = 0.7) +
            scaleYLog10() +
            geomText(vjust = "top", color = "black") { label = "label" } +
            ggtitle(title) +
            labs(x = xAxisLabel, y = "LOC (log scale)")

        val outputFileName = fileName.replace("/", "_")
        val outputFile = File(outputDir.asFile.get(), "$outputFileName.png")
        ggsave(plot, outputFile.name, path = outputFile.parent)
        logger.lifecycle("Generated chart for $fileName: ${outputFile.absolutePath}")
    }

    private fun generateBuildFilesCategoryChart(data: List<TargetLoc>) {
        val aggregated = data.groupBy { it.module }
            .mapValues { it.value.sumOf { loc -> loc.loc } }
            .toList()
            .sortedByDescending { it.second }

        if (aggregated.isEmpty()) return

        val totalLoc = aggregated.sumOf { it.second }
        val categories = aggregated.map { it.first }
        val locs = aggregated.map { max(it.second, 1) }
        val labels = aggregated.map { (_, loc) ->
            val percentage = (loc.toDouble() / totalLoc * 100).roundToInt()
            "$loc\n($percentage%)"
        }

        val dataMap = mapOf(
            "category" to categories,
            "loc" to locs,
            "label" to labels,
        )

        val plot = ggplot(dataMap) {
            x = "category"
            y = "loc"
        } +
            geomBar(stat = Stat.identity, color = "#4682B4", fill = "#4682B4", alpha = 0.7) +
            scaleYLog10() +
            geomText(vjust = "top", color = "black") { label = "label" } +
            ggtitle("Lines of Code - Build Files by Category ($totalLoc total)") +
            labs(x = "Category", y = "LOC (log scale)")

        val outputFile = File(outputDir.asFile.get(), "build.png")
        ggsave(plot, outputFile.name, path = outputFile.parent)
        logger.lifecycle("Generated build files chart: ${outputFile.absolutePath}")
    }

    private fun generateComparisonChart(data: List<TargetLoc>) {
        val aggregated = data.groupBy { Pair(it.target, it.type) }
            .mapValues { it.value.sumOf { loc -> loc.loc } }
            .filter { it.value > 0 }

        if (aggregated.isEmpty()) return

        val totalByTarget = aggregated.entries.groupBy { it.key.first }
            .mapValues { it.value.sumOf { entry -> entry.value } }

        val entries = aggregated.entries.toList()

        val targets = entries.map { it.key.first }
        val locs = entries.map { max(it.value, 1) }
        val types = entries.map { it.key.second }
        val labels = entries.map { (key, value) ->
            val targetTotal = totalByTarget[key.first] ?: value
            val percentage = (value.toDouble() / targetTotal * 100).roundToInt()
            "$value\n($percentage%)"
        }

        val chartData = mapOf(
            "target" to targets,
            "loc" to locs,
            "type" to types,
            "label" to labels,
        )

        val plot = ggplot(chartData) {
            x = "target"
            y = "loc"
            fill = "type"
        } +
            geomBar(stat = Stat.identity, position = positionDodge(), alpha = 0.7) +
            scaleYLog10() +
            geomText(position = positionDodge(width = 0.9), vjust = "top", color = "black") { label = "label" } +
            ggtitle("Lines of Code by Type - All Modules") +
            labs(x = "Target Type", y = "LOC (log scale)", fill = "Code Type")

        val outputFile = File(outputDir.asFile.get(), "comparison_by_type.png")
        ggsave(plot, outputFile.name, path = outputFile.parent)
        logger.lifecycle("Generated comparison chart: ${outputFile.absolutePath}")
    }

    private fun getModuleTypeColor(moduleType: String): String = when (moduleType) {
        "Core" -> "#DC143C" // Crimson
        "App" -> "#FF8C00" // Dark Orange
        "Server" -> "#4169E1" // Royal Blue
        "Build" -> "#8B4513" // Saddle Brown
        else -> "#808080" // Gray
    }

    private fun getModuleType(module: String) = when {
        module == "core" -> "Core"
        module.startsWith("app/") -> "App"
        module.startsWith("server/") -> "Server"
        module == "build" -> "Build"
        else -> null
    }

    private fun printSummary(locData: List<TargetLoc>) {
        logger.lifecycle("\n=== LOC Summary ===\n")

        locData.groupBy { it.module }.forEach { (module, data) ->
            logger.lifecycle("Module: $module")
            data.groupBy { it.target }.forEach { (target, targetData) ->
                val total = targetData.sumOf { it.loc }
                val breakdown = targetData.groupBy { it.type }
                    .mapValues { it.value.sumOf { loc -> loc.loc } }
                logger.lifecycle("  $target: $total lines ${breakdown.entries.joinToString(", ") { "(${it.key}: ${it.value})" }}")
            }
            logger.lifecycle("")
        }

        logger.lifecycle("=== Totals by Target ===")
        locData.groupBy { it.target }
            .mapValues { it.value.sumOf { loc -> loc.loc } }
            .toList()
            .sortedByDescending { it.second }
            .forEach { (target, total) -> logger.lifecycle("  $target: $total lines") }

        logger.lifecycle("\n=== Totals by Type ===")
        locData.groupBy { it.type }
            .mapValues { it.value.sumOf { loc -> loc.loc } }
            .forEach { (type, total) -> logger.lifecycle("  $type: $total lines") }

        val grandTotal = locData.sumOf { it.loc }
        logger.lifecycle("\nGrand Total: $grandTotal lines")

        logger.lifecycle("\n=== Percentage by Module ===")
        locData.groupBy { it.module }
            .mapValues { it.value.sumOf { loc -> loc.loc } }
            .toList()
            .sortedByDescending { it.second }
            .forEach { (module, total) ->
                val percentage = (total.toDouble() / grandTotal * 100).roundToInt()
                logger.lifecycle("  $module: $total lines ($percentage%)")
            }
    }
}
