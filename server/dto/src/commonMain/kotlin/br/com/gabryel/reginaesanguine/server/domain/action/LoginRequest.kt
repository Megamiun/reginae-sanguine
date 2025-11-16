package br.com.gabryel.reginaesanguine.server.domain.action

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
)
