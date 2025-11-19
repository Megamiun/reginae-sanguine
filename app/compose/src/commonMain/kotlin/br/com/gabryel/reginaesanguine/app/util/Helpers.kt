package br.com.gabryel.reginaesanguine.app.util

import br.com.gabryel.reginaesanguine.app.services.ResourceLoader
import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.domain.parser.gameJsonParser
import br.com.gabryel.reginaesanguine.viewmodel.pack.PackClient

val parser = gameJsonParser()

suspend fun getStandardPack(resourceLoader: ResourceLoader): Pack {
    val resource = resourceLoader.load("packs/queens_blood/pack_info.json").decodeToString()
    return parser.decodeFromString<Pack>(resource)
}

suspend fun getPacksFromServer(packClient: PackClient): List<Pack> =
    packClient.getAllPacks()

suspend fun getStandardPackFromServer(packClient: PackClient): Pack {
    val packs = getPacksFromServer(packClient)
    return packs.firstOrNull { it.id.contains("queens_blood", ignoreCase = true) }
        ?: packs.firstOrNull()
        ?: error("No packs available from server")
}
