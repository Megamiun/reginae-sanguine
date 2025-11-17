package br.com.gabryel.reginaesanguine.server.controller

import br.com.gabryel.reginaesanguine.server.domain.DeckDto
import br.com.gabryel.reginaesanguine.server.domain.DeckPageDto
import br.com.gabryel.reginaesanguine.server.domain.action.CreateDeckRequest
import br.com.gabryel.reginaesanguine.server.domain.action.UpdateDeckRequest
import br.com.gabryel.reginaesanguine.server.service.AccountDeckService
import br.com.gabryel.reginaesanguine.server.service.security.TokenService
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("user-deck", produces = [APPLICATION_JSON_VALUE])
class AccountDeckController(
    private val accountDeckService: AccountDeckService,
    private val tokenService: TokenService,
) {
    @PostMapping
    @ResponseStatus(CREATED)
    fun createDeck(
        @RequestHeader("Authorization") authorization: String,
        @RequestBody request: CreateDeckRequest,
    ): DeckDto = runBlocking {
        val accountId = extractAccountId(authorization)
        try {
            accountDeckService.create(accountId, request)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(BAD_REQUEST, e.message, e)
        }
    }

    @GetMapping
    fun getAllDecks(
        @RequestHeader("Authorization") authorization: String,
    ): DeckPageDto = runBlocking {
        val accountId = extractAccountId(authorization)
        accountDeckService.getAllByAccountId(accountId).upcast(::DeckPageDto)
    }

    @GetMapping("{deckId}")
    fun getDeck(
        @RequestHeader("Authorization") authorization: String,
        @PathVariable deckId: String,
    ): DeckDto = runBlocking {
        val accountId = extractAccountId(authorization)
        try {
            accountDeckService.getById(accountId, deckId)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(BAD_REQUEST, e.message, e)
        }
    }

    @PutMapping("{deckId}")
    fun updateDeck(
        @RequestHeader("Authorization") authorization: String,
        @PathVariable deckId: String,
        @RequestBody request: UpdateDeckRequest,
    ): DeckDto = runBlocking {
        val accountId = extractAccountId(authorization)
        try {
            accountDeckService.update(accountId, deckId, request)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(BAD_REQUEST, e.message, e)
        }
    }

    private fun extractAccountId(authorization: String): String {
        val token = authorization.removePrefix("Bearer ").trim()
        return tokenService.validateToken(token)
            ?: throw ResponseStatusException(UNAUTHORIZED, "Invalid or expired token")
    }
}
