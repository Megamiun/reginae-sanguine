package br.com.gabryel.reginaesanguine.domain

import arrow.core.raise.ensure
import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.Action.Skip
import br.com.gabryel.reginaesanguine.domain.Failure.GameEnded
import br.com.gabryel.reginaesanguine.domain.Failure.NotPlayerTurn
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.State.Ended
import br.com.gabryel.reginaesanguine.domain.State.Ended.Tie
import br.com.gabryel.reginaesanguine.domain.State.Ongoing
import br.com.gabryel.reginaesanguine.domain.util.buildResult

data class Game(
    private val moveFrom: PlayerPosition,
    private val board: Board,
    val players: Map<PlayerPosition, Player>,
    val action: Action<out String>? = null,
    val previous: Game? = null
) : CellContainer by board {
    val round: Int = 1 + (previous?.round ?: 0)

    val nextPlayer = when (moveFrom) {
        LEFT -> RIGHT
        RIGHT -> LEFT
    }

    companion object {
        fun forPlayers(left: Player, right: Player, drawn: Int = 5) =
            Game(RIGHT, Board.default(), mapOf(LEFT to left.draw(drawn), RIGHT to right.draw(drawn)))
    }

    fun play(player: PlayerPosition, action: Action<out String>): Result<Game> = buildResult {
        ensure(nextPlayer == player) { NotPlayerTurn(this@Game) }
        ensure(getState() !is Ended) { GameEnded }

        val otherPlayerAfterDraw = players.getValue(player).draw()

        when (action) {
            is Skip -> copy(
                moveFrom = player,
                players = players + mapOf(player.opponent to otherPlayerAfterDraw),
                previous = copy(action = action),
            )
            is Play -> {
                val (playerAfterPlay, card) = players
                    .getValue(player)
                    .selectCard(action.card)
                    .orRaiseError()

                val newPlayers = mapOf(player to playerAfterPlay, player.opponent to otherPlayerAfterDraw)

                val newBoard = board.play(player, Play(action.position, card)).orRaiseError()
                copy(moveFrom = player, players = newPlayers, board = newBoard, previous = copy(action = action))
            }
        }
    }

    fun getScores(): Map<PlayerPosition, Int> = board.getScores()

    fun getLaneScore(lane: Int): Map<PlayerPosition, Int> = board.getLaneScores(lane)

    fun getState(): State =
        if (previous?.action == Skip && previous.previous?.action == Skip)
            Tie
        else
            Ongoing
}
