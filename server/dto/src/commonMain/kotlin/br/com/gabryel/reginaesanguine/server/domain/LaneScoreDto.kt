package br.com.gabryel.reginaesanguine.server.domain

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import kotlinx.serialization.Serializable

/**
 * Represents the scores for a single lane.
 */
@Serializable
data class LaneScoreDto(
    val lane: Int,
    val scores: List<PlayerScoreDto>
) {
    companion object {
        fun from(lane: Int, scores: Map<PlayerPosition, Int>) = LaneScoreDto(
            lane = lane,
            scores = scores.map { (player, score) -> PlayerScoreDto.from(player, score) },
        )
    }
}
