package br.com.gabryel.reginaesanguine.server.controller

import br.com.gabryel.reginaesanguine.server.domain.AccountDto
import br.com.gabryel.reginaesanguine.server.domain.action.CreateAccountRequest
import br.com.gabryel.reginaesanguine.server.domain.action.LoginRequest
import br.com.gabryel.reginaesanguine.server.domain.action.LoginResponse
import br.com.gabryel.reginaesanguine.server.service.AccountService
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("account", produces = [APPLICATION_JSON_VALUE])
class AccountController(private val accountService: AccountService) {
    @PostMapping
    @ResponseStatus(CREATED)
    fun createAccount(
        @RequestBody request: CreateAccountRequest
    ): AccountDto = runBlocking {
        try {
            accountService.create(request)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(BAD_REQUEST, e.message, e)
        }
    }

    @PostMapping("login")
    fun login(
        @RequestBody request: LoginRequest
    ): LoginResponse = runBlocking {
        try {
            accountService.login(request)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(BAD_REQUEST, e.message, e)
        }
    }
}
