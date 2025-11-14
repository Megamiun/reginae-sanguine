package br.com.gabryel.reginaesanguine.app.util

import br.com.gabryel.reginaesanguine.app.client.PackClient
import br.com.gabryel.reginaesanguine.app.services.ResourceLoader
import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.domain.parser.gameJsonParser
import br.com.gabryel.reginaesanguine.server.client.KtorServerClient

val parser = gameJsonParser()

suspend fun getStandardPack(resourceLoader: ResourceLoader): Pack {
    val resource = resourceLoader.load("packs/queens_blood/pack_info.json").decodeToString()
    return parser.decodeFromString<Pack>(resource)
}

suspend fun getPacksFromServer(serverUrl: String = "http://localhost:8080"): List<Pack> {
    val client = KtorServerClient(serverUrl, gameJsonParser())
    val packClient = PackClient(client)
    return try {
        packClient.getAllPacks()
    } finally {
        client.close()
    }
}

suspend fun getStandardPackFromServer(serverUrl: String = "http://localhost:8080"): Pack {
    val packs = getPacksFromServer(serverUrl)
    return packs.firstOrNull { it.id.contains("queens_blood", ignoreCase = true) }
        ?: packs.firstOrNull()
        ?: error("No packs available from server at $serverUrl")
}
