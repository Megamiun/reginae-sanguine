package br.com.gabryel.reginaesanguine.server.controller

import br.com.gabryel.reginaesanguine.server.domain.PackDto
import br.com.gabryel.reginaesanguine.server.service.DeckService
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("deck", produces = [APPLICATION_JSON_VALUE])
class DeckController(private val deckService: DeckService) {
    @GetMapping("pack/{packId}")
    fun getPack(
        @PathVariable packId: String
    ): PackDto = try {
        deckService.getPack(packId)
    } catch (e: IllegalArgumentException) {
        throw ResponseStatusException(NOT_FOUND, e.message, e)
    }
}
