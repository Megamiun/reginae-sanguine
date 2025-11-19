package br.com.gabryel.reginaesanguine.server.domain

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import kotlinx.serialization.Serializable

/**
 * Response DTO when a lobby has matched players and started a game.
 */
@Serializable
data class GameRequestStatusDto(
    val status: GameRequestStatus,
    val gameId: String? = null,
    val myPosition: PlayerPosition? = null
)
