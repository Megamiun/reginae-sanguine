package br.com.gabryel.reginaesanguine.domain.parser

import br.com.gabryel.reginaesanguine.domain.Card
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.test.Test

class ParserTest {
    @Test
    fun `should be able to parse json file without errors`() {
        val content = ParserTest::class.java.getResourceAsStream("/standard_pack_cards.json")
        val gameJsonParser = gameJsonParser()
        val decodeFromString = gameJsonParser.decodeFromString<List<Card>>(content.bufferedReader().readText())
        val encodeToStrong = gameJsonParser.encodeToString(decodeFromString)

        decodeFromString shouldHaveSize 166
    }
}
