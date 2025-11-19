package br.com.gabryel.reginaesanguine.server.domain.action

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import kotlinx.serialization.Serializable

/**
 * Request DTO for initializing a new game.
 */
@Serializable
data class InitGameRequest(
    val creatorId: String,
    val creatorDeckStateId: String,
    val creatorPosition: PlayerPosition,
    val joinerId: String,
    val joinerDeckStateId: String,
)
