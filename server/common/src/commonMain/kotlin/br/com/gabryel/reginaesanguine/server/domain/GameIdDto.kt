package br.com.gabryel.reginaesanguine.server.domain

import kotlinx.serialization.Serializable

/**
 * Response DTO for game initialization.
 */
@Serializable
data class GameIdDto(
    val gameId: String
)
