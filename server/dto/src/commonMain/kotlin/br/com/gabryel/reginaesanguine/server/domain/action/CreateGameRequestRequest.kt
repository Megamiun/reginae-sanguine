package br.com.gabryel.reginaesanguine.server.domain.action

import kotlinx.serialization.Serializable

/**
 * Request DTO for creating a new game request.
 */
@Serializable
data class CreateGameRequestRequest(
    val deckStateId: String,
)