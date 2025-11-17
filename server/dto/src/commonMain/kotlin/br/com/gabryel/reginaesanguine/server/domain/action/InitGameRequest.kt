package br.com.gabryel.reginaesanguine.server.domain.action

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import kotlinx.serialization.Serializable

/**
 * Request DTO for initializing a new game.
 */
@Serializable
data class InitGameRequest(
    val deckStateId: String,
    val position: PlayerPosition,
)
