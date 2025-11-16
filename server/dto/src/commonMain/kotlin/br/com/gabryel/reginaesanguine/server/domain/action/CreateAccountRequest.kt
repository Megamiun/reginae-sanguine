package br.com.gabryel.reginaesanguine.server.domain.action

import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountRequest(
    val username: String,
    val email: String,
    val password: String,
)
