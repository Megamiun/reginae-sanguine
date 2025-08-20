package br.com.gabryel.reginaesanguine.app.util

import br.com.gabryel.reginaesanguine.app.services.ResourceLoader
import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.domain.parser.gameJsonParser

val parser = gameJsonParser()

suspend fun getStandardPack(resourceLoader: ResourceLoader): Pack {
    val resource = resourceLoader.load("packs/queens_blood/pack_info.json").decodeToString()
    return parser.decodeFromString<Pack>(resource)
}
