package br.com.gabryel.reginaesanguine.server.controller

import br.com.gabryel.reginaesanguine.server.domain.PackDto
import br.com.gabryel.reginaesanguine.server.domain.page.PackPageDto
import br.com.gabryel.reginaesanguine.server.service.DeckService
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("pack", produces = [APPLICATION_JSON_VALUE])
class PackController(private val deckService: DeckService) {
    @GetMapping
    fun getPacks(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): PackPageDto = runBlocking {
        deckService.loadPacks(page, size)
    }

    @GetMapping("{packId}")
    fun getPack(
        @PathVariable packId: String
    ): PackDto = runBlocking {
        val pack = deckService.loadPack(packId)
            ?: throw ResponseStatusException(NOT_FOUND, "Pack $packId not found")
        PackDto.from(pack)
    }
}
