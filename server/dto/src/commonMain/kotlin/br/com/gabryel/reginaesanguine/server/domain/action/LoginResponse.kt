package br.com.gabryel.reginaesanguine.server.domain.action

import br.com.gabryel.reginaesanguine.server.domain.AccountDto
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val account: AccountDto,
    val token: String,
)
