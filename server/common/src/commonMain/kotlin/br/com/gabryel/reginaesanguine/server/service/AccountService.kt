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

        require(!accountRepository.existsByUsername(request.username)) { "Username already exists" }
        require(!accountRepository.existsByEmail(request.email)) { "Email already exists" }

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

        val account = requireNotNull(accountRepository.findByUsername(request.username)) {
            "Invalid username or password"
        }
        require(passwordHasher.verify(request.password, account.passwordHash)) { "Invalid username or password" }

        val token = tokenService.generateToken(account.id)
        return LoginResponse(account.toDto(), token)
    }

    private fun Account.toDto() = AccountDto(id = id, username = username, email = email)
}
