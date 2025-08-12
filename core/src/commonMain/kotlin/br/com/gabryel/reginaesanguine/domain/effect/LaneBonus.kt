package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.collections.emptyMap

/**
 * This file contains all effects that implement the LaneBonus interface.
 * LaneBonus effects modify lane power values on the board.
 */
interface LaneBonus : Effect {
    fun getRaiseLaneAmounts(game: GameSummarizer, source: PlayerPosition, sourcePosition: Position): Map<Int, Map<PlayerPosition, Int>>
}

@Serializable
@SerialName("ScoreBonus")
class ScoreBonus(
    val amount: Int,
    override val description: String = "Add $amount score to lane if you wins lane",
) : LaneBonus {
    @Transient
    override val trigger = WhenLaneWon

    override fun getRaiseLaneAmounts(
        game: GameSummarizer,
        source: PlayerPosition,
        sourcePosition: Position
    ): Map<Int, Map<PlayerPosition, Int>> {
        val lane = sourcePosition.lane
        val laneScores = game.getBaseLaneScoreAt(lane)

        val maxValue = laneScores.maxOf { it.value }
        val opponentScore = laneScores[source.opponent] ?: 0

        return if (maxValue > opponentScore)
            mapOf(lane to mapOf(source to amount))
        else emptyMap()
    }
}

@Serializable
@SerialName("LoserScoreBonus")
class LoserScoreBonus(
    override val description: String = "Add points from every lane loser score to winner score",
) : LaneBonus {
    @Transient
    override val trigger = WhenLaneWon

    override fun getRaiseLaneAmounts(
        game: GameSummarizer,
        source: PlayerPosition,
        sourcePosition: Position
    ): Map<Int, Map<PlayerPosition, Int>> =
        (0..2).associateWith { lane ->
            val laneScores = game.getBaseLaneScoreAt(lane)

            val maxValue = laneScores.maxOf { it.value }
            val winner = laneScores
                .filter { it.value == maxValue }
                .map { it.key }

            if (winner.size != 1)
                return@associateWith emptyMap()

            mapOf(winner.first() to laneScores.minOf { it.value })
        }
}
