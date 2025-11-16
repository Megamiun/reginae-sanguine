package br.com.gabryel.reginaesanguine.server.service

import br.com.gabryel.reginaesanguine.server.domain.Account
import br.com.gabryel.reginaesanguine.server.domain.AccountDto
import br.com.gabryel.reginaesanguine.server.domain.action.CreateAccountRequest
import br.com.gabryel.reginaesanguine.server.domain.action.LoginRequest
import br.com.gabryel.reginaesanguine.server.domain.action.LoginResponse
import br.com.gabryel.reginaesanguine.server.repository.AccountRepository
import br.com.gabryel.reginaesanguine.server.service.security.PasswordHasher
import br.com.gabryel.reginaesanguine.server.service.security.TokenService
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AccountService(
    private val accountRepository: AccountRepository,
    private val passwordHasher: PasswordHasher,
    private val tokenService: TokenService,
) {
    suspend fun create(request: CreateAccountRequest): AccountDto {
        require(request.username.isNotBlank()) { "Username cannot be empty" }
        require(request.email.isNotBlank()) { "Email cannot be empty" }
        require(request.password.isNotBlank()) { "Password cannot be empty" }

        if (accountRepository.existsByUsername(request.username))
            throw IllegalArgumentException("Username already exists")

        if (accountRepository.existsByEmail(request.email))
            throw IllegalArgumentException("Email already exists")

        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val account = Account(
            id = "",
            username = request.username,
            email = request.email,
            passwordHash = passwordHasher.hash(request.password),
            createdAt = now,
            updatedAt = now,
        )

        val savedAccount = accountRepository.save(account)
        return savedAccount.toDto()
    }

    suspend fun login(request: LoginRequest): LoginResponse {
        require(request.username.isNotBlank()) { "Username cannot be empty" }
        require(request.password.isNotBlank()) { "Password cannot be empty" }

        val account = accountRepository.findByUsername(request.username)
            ?: throw IllegalArgumentException("Invalid username or password")

        if (!passwordHasher.verify(request.password, account.passwordHash))
            throw IllegalArgumentException("Invalid username or password")

        val token = tokenService.generateToken(account.id)
        return LoginResponse(account.toDto(), token)
    }

    private fun Account.toDto() = AccountDto(id = id, username = username, email = email)
}
