package br.com.gabryel.reginaesanguine.server.controller

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.server.domain.ActionDto
import br.com.gabryel.reginaesanguine.server.domain.GameIdDto
import br.com.gabryel.reginaesanguine.server.domain.GameViewDto
import br.com.gabryel.reginaesanguine.server.domain.action.InitGameRequest
import br.com.gabryel.reginaesanguine.server.service.GameService
import kotlinx.coroutines.runBlocking
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
class GameController(private val gameService: GameService) {
    @PostMapping
    fun initGame(
        @RequestBody request: InitGameRequest
    ): GameIdDto = runBlocking {
        try {
            val gameId = gameService.initGame(request)
            GameIdDto(gameId)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(BAD_REQUEST, e.message, e)
        }
    }

    @PostMapping("/{gameId}/action")
    fun executeAction(
        @PathVariable gameId: String,
        @RequestHeader("Authorization") authorization: String,
        @RequestBody actionDto: ActionDto
    ): GameViewDto = try {
        val playerPosition = PlayerPosition.valueOf(authorization)
        val action = actionDto.toDomain()
        gameService.executeAction(gameId, playerPosition, action)
    } catch (e: IllegalArgumentException) {
        throw ResponseStatusException(BAD_REQUEST, e.message, e)
    }

    @GetMapping("/{gameId}/status")
    fun fetchStatus(
        @PathVariable gameId: String,
        @RequestHeader("Authorization") authorization: String
    ): GameViewDto? {
        val playerPosition = PlayerPosition.valueOf(authorization)
        return gameService.fetchStatus(gameId, playerPosition)
    }
}
