package br.com.gabryel.reginaesanguine.server.domain.action

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import kotlinx.serialization.Serializable

/**
 * Request DTO for initializing a new game.
 */
@Serializable
data class InitGameRequest(
    val deckCardIds: List<String>,
    val position: PlayerPosition,
    val packId: String
)
