package br.com.gabryel.reginaesanguine.server.domain.action

import kotlinx.serialization.Serializable

/**
 * Request DTO for joining an existing game request.
 */
@Serializable
data class JoinGameRequestRequest(
    val deckStateId: String,
)
