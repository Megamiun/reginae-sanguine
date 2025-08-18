package br.com.gabryel.reginaesanguine.server.controller

import br.com.gabryel.reginaesanguine.domain.Pack
import kotlinx.serialization.json.Json
import org.springframework.http.ResponseEntity
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
    ): ResponseEntity<Pack> {
        val resource = javaClass.getResourceAsStream("/packs/$packId/pack_info.json")
            ?: return ResponseEntity.notFound().build()

        val pack = json.decodeFromString<Pack>(resource.bufferedReader().readText())

        return ResponseEntity.ok(pack)
    }
}
