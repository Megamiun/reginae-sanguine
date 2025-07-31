package br.com.gabryel.reginaesanguine.app.util

import br.com.gabryel.reginaesanguine.app.services.ResourceLoader
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.parser.gameJsonParser
import okio.internal.commonToUtf8String

val parser = gameJsonParser()

fun createTestDeck(resourceLoader: ResourceLoader): List<Card> {
    val resource = resourceLoader.load("standard_pack_cards.json").commonToUtf8String()
    return parser.decodeFromString<List<Card>>(resource)
}
