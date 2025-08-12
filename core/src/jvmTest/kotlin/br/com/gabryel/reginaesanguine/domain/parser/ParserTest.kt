package br.com.gabryel.reginaesanguine.domain.parser

import br.com.gabryel.reginaesanguine.domain.Card
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.test.Test

class ParserTest {
    @Test
    fun `should be able to parse json file without errors`() {
        val parser = gameJsonParser()
        val content = ParserTest::class.java.getResourceAsStream("/standard_pack_cards.json")

        parser.decodeFromString<List<Card>>(content.bufferedReader().readText()) shouldHaveSize 166
    }
}
