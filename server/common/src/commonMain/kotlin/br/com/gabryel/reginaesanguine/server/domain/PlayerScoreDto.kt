package br.com.gabryel.reginaesanguine.server.domain

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import kotlinx.serialization.Serializable

/**
 * Represents a player's score.
 */
@Serializable
data class PlayerScoreDto(
    val player: PlayerPosition,
    val score: Int
) {
    companion object {
        fun from(player: PlayerPosition, score: Int) = PlayerScoreDto(player, score)
    }
}
