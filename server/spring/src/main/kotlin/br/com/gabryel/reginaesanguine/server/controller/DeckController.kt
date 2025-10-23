package br.com.gabryel.reginaesanguine.server.controller

import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.server.domain.PackDto
import kotlinx.serialization.json.Json
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("deck")
class DeckController(private val json: Json) {
    @GetMapping("pack/{packId}")
    fun getPack(
        @PathVariable packId: String
    ): PackDto {
        val resource = javaClass.getResourceAsStream("/packs/$packId/pack_info.json")
            ?: throw ResponseStatusException(NOT_FOUND, "Pack $packId not found")

        val pack = json.decodeFromString<Pack>(resource.bufferedReader().readText())

        return PackDto.from(pack)
    }
}
