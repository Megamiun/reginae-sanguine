package br.com.gabryel.reginaesanguine.server.domain

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import kotlinx.serialization.Serializable

/**
 * Represents the winner of a single lane.
 */
@Serializable
data class LaneWinnerDto(
    val lane: Int,
    val winner: PlayerPosition?
) {
    companion object {
        fun from(lane: Int, winner: PlayerPosition?) = LaneWinnerDto(lane, winner)
    }
}
