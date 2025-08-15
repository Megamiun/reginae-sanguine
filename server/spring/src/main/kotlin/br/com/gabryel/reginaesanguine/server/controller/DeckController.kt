package br.com.gabryel.reginaesanguine.server.controller

import br.com.gabryel.reginaesanguine.domain.Pack
import kotlinx.serialization.json.Json
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("deck")
class DeckController(private val json: Json) {
    @GetMapping("pack/{packId}")
    fun getCards(
        @PathVariable packId: String
    ): Pack = when (packId) {
        "queens_blood" -> {
            val resource = javaClass.getResourceAsStream("/queens_blood_pack_info.json")
            check(resource != null) { "queens_blood pack info not found" }

            val pack = json.decodeFromString<Pack>(resource.bufferedReader().readText())

            pack
        }
        // TODO Change to notFound() after fixed: https://github.com/spring-projects/spring-framework/issues/35281
        else -> error("A")
    }
}
