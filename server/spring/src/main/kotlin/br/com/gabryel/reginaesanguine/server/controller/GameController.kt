package br.com.gabryel.reginaesanguine.server.controller

import br.com.gabryel.reginaesanguine.server.domain.ActionDto
import br.com.gabryel.reginaesanguine.server.domain.GameViewDto
import br.com.gabryel.reginaesanguine.server.service.GameService
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("game", produces = [APPLICATION_JSON_VALUE])
class GameController(private val gameService: GameService, private val authHelper: AuthHelper) {
    @PostMapping("/{gameId}/action")
    fun executeAction(
        @PathVariable gameId: String,
        @RequestHeader("Authorization") authorization: String,
        @RequestBody actionDto: ActionDto
    ): GameViewDto = try {
        val accountId = authHelper.extractAccountId(authorization)
        val action = actionDto.toDomain()
        gameService.executeAction(gameId, accountId, action)
    } catch (e: IllegalArgumentException) {
        throw ResponseStatusException(BAD_REQUEST, e.message, e)
    }

    @GetMapping("/{gameId}/status")
    fun fetchStatus(
        @PathVariable gameId: String,
        @RequestHeader("Authorization") authorization: String
    ): GameViewDto? {
        val accountId = authHelper.extractAccountId(authorization)
        return gameService.fetchStatus(gameId, accountId)
    }
}
