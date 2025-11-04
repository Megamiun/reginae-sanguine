package br.com.gabryel.reginaesanguine.server.node.service

import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.server.service.DeckService
import kotlinx.serialization.json.Json

val fs = js("require('fs')")
val path = js("require('path')")

class NodeDeckService(private val json: Json) : DeckService() {
    override fun loadPack(packId: String): Pack? = try {
        val content = getFileContent(packId) ?: return null
        json.decodeFromString<Pack>(content)
    } catch (e: Throwable) {
        console.error("Failed to load pack $packId: ${e.message}")
        null
    }

    private fun getFileContent(packId: String): String? {
        val locations = listOf(
            path.resolve(js("__dirname"), "..", "..", "..", "..", "..", "assets", "packs", packId, "pack_info.json"),
            path.resolve(js("__dirname"), "..", "..", "..", "..", "assets", "packs", packId, "pack_info.json"),
            path.join(js("process.cwd()"), "assets", "packs", packId, "pack_info.json"),
            path.join(js("process.cwd()"), "reginae-sanguine", "assets", "packs", packId, "pack_info.json"),
        )

        val existingPath = locations.firstOrNull { fs.existsSync(it) } ?: return null
        return fs.readFileSync(existingPath, "utf8") as String
    }
}
