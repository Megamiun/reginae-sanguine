package br.com.gabryel.reginaesanguine.server.controller

import br.com.gabryel.reginaesanguine.server.domain.GameRequestDto
import br.com.gabryel.reginaesanguine.server.domain.page.GameRequestPageDto
import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatusDto
import br.com.gabryel.reginaesanguine.server.domain.action.CreateGameRequestRequest
import br.com.gabryel.reginaesanguine.server.domain.action.JoinGameRequestRequest
import br.com.gabryel.reginaesanguine.server.service.Lobby
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("game-request", produces = [APPLICATION_JSON_VALUE])
class GameRequestController(
    private val lobby: Lobby,
    private val authHelper: AuthHelper,
) {
    @PostMapping
    fun createGameRequest(
        @RequestHeader("Authorization") authorization: String,
        @RequestBody request: CreateGameRequestRequest
    ): GameRequestDto = runBlocking {
        try {
            val accountId = authHelper.extractAccountId(authorization)
            lobby.createGameRequest(accountId, request)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(BAD_REQUEST, e.message, e)
        }
    }

    @GetMapping
    fun listGameRequests(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): GameRequestPageDto = runBlocking {
        val pageDto = lobby.listAvailableGameRequests(page, size)
        GameRequestPageDto(
            content = pageDto.content,
            page = pageDto.page,
            size = pageDto.size,
            totalElements = pageDto.totalElements,
            totalPages = pageDto.totalPages
        )
    }

    @PostMapping("/{requestId}/join")
    fun joinGameRequest(
        @PathVariable requestId: String,
        @RequestHeader("Authorization") authorization: String,
        @RequestBody request: JoinGameRequestRequest
    ): GameRequestStatusDto = runBlocking {
        try {
            val accountId = authHelper.extractAccountId(authorization)
            lobby.joinGameRequest(requestId, accountId, request)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(BAD_REQUEST, e.message, e)
        }
    }

    @GetMapping("/{requestId}/status")
    fun getGameRequestStatus(
        @PathVariable requestId: String,
        @RequestHeader("Authorization") authorization: String
    ) = runBlocking {
        val accountId = authHelper.extractAccountId(authorization)
        val result = lobby.getGameRequestStatus(requestId, accountId)
        result?.let { ResponseEntity.ok(it) } ?: ResponseEntity.noContent().build()
    }
}
