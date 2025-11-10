package br.com.gabryel.reginaesanguine.server.node.service

import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.domain.parser.gameJsonParser
import br.com.gabryel.reginaesanguine.server.node.require
import br.com.gabryel.reginaesanguine.server.service.PackLoader

/**
 * Node.js implementation of PackLoader using filesystem access.
 * Note: This is a synchronous implementation for simplicity.
 */
class NodePackLoader : PackLoader {
    private val fs = require("fs")
    private val json = gameJsonParser()

    override fun loadAllPacks(): List<Pack> {
        val knownPacks = listOf("queens_blood")

        return knownPacks.mapNotNull { packId ->
            try {
                loadPack(packId)
            } catch (e: Throwable) {
                console.error("Error loading pack $packId: ${e.message}")
                null
            }
        }
    }

    private fun loadPack(packId: String): Pack? {
        return try {
            val possiblePaths = listOf("kotlin/packs/$packId/pack_info.json")

            val resourcePath = possiblePaths.firstOrNull { fileExistsSync(it) }

            if (resourcePath == null) {
                console.error("Pack file not found. Tried paths: ${possiblePaths.joinToString()}")
                return null
            }

            console.log("About to load pack from: $resourcePath")
            val content = readFileSync(resourcePath)
            val pack = json.decodeFromString<Pack>(content)
            pack
        } catch (e: Throwable) {
            console.error("Error loading pack $packId: ${e.message}")
            e.printStackTrace()
            null
        } catch (e: dynamic) {
            console.error("Error loading pack $packId: $e")
            e.printStackTrace()
            null
        }
    }

    private fun fileExistsSync(path: String): Boolean = try {
        fs.existsSync(path) as Boolean
    } catch (e: Throwable) {
        false
    }

    private fun readFileSync(path: String): String = fs.readFileSync(path, "utf8") as String
}
