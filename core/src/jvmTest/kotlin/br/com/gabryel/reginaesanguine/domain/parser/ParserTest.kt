package br.com.gabryel.reginaesanguine.domain.parser

import br.com.gabryel.reginaesanguine.domain.Pack
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.test.Test

class ParserTest {
    @Test
    fun `should be able to parse json file without errors`() {
        val parser = gameJsonParser()
        val content = javaClass.getResourceAsStream("/packs/queens_blood/pack_info.json")

        parser.decodeFromString<Pack>(content.bufferedReader().readText()).cards shouldHaveSize 166
    }
}
