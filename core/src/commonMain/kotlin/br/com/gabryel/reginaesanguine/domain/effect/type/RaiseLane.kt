package br.com.gabryel.reginaesanguine.domain.effect.type

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.effect.GameSummarizer
import br.com.gabryel.reginaesanguine.domain.effect.WhenLaneWon
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * This file contains all effects that implement the RaiseLane interface.
 * RaiseLane effects modify lane power values on the board.
 */
interface RaiseLane : Effect {
    fun getRaiseLaneAmounts(game: GameSummarizer, source: PlayerPosition, sourcePosition: Position): Map<Int, Map<PlayerPosition, Int>>
}

@Serializable
@SerialName("RaiseLaneIfWon")
class RaiseLaneIfWon(
    val amount: Int,
    override val description: String = "Add $amount score to lane if you wins lane",
) : RaiseLane {
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

    override val discriminator: String = "RaiseLaneIfWon"
}

@Serializable
@SerialName("RaiseWinnerLanesByLoserScore")
class RaiseWinnerLanesByLoserScore(
    override val description: String = "Add points from every lane loser score to winner score",
) : RaiseLane {
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

    override val discriminator: String = "RaiseWinnerLanesByLoserScore"
}
