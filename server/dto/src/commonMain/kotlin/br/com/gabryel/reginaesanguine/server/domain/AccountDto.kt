package br.com.gabryel.reginaesanguine.server.domain

import kotlinx.serialization.Serializable

@Serializable
data class AccountDto(
    val id: String,
    val username: String,
    val email: String,
)
