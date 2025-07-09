package br.com.gabryel.reginarsanguine.domain

import arrow.core.raise.ensure
import br.com.gabryel.reginarsanguine.domain.Action.Play
import br.com.gabryel.reginarsanguine.domain.Action.Skip
import br.com.gabryel.reginarsanguine.domain.Failure.GameEnded
import br.com.gabryel.reginarsanguine.domain.Failure.NotPlayerTurn
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginarsanguine.domain.State.Ended
import br.com.gabryel.reginarsanguine.domain.State.Ended.Tie
import br.com.gabryel.reginarsanguine.domain.State.Ongoing
import br.com.gabryel.reginarsanguine.util.buildResult

data class Game(
    private val moveFrom: PlayerPosition,
    private val board: Board,
    val players: Map<PlayerPosition, Player>,
    val action: Action<out String>? = null,
    val previous: Game? = null
) : CellContainer by board {
    val nextPlayer = when (moveFrom) {
        LEFT -> RIGHT
        RIGHT -> LEFT
    }

    companion object {
        fun forPlayers(left: Player, right: Player) =
            Game(RIGHT, Board.default(), mapOf(LEFT to left, RIGHT to right))
    }

    fun play(
        position: PlayerPosition,
        action: Action<out String>,
    ): Result<Game> = buildResult {
        ensure(nextPlayer == position) { NotPlayerTurn(this@Game) }
        ensure(getState() !is Ended) { GameEnded }

        val otherPlayerAfterDraw = players.getValue(position).draw()

        when (action) {
            is Skip -> copy(moveFrom = position, players = players + mapOf(position.next to otherPlayerAfterDraw), previous = copy(action = action))
            is Play -> {
                val (playerAfterPlay, card) = players.getValue(position)
                    .selectCard(action.card)
                    .orRaiseError()

                val newPlayers = mapOf(position to playerAfterPlay, position.next to otherPlayerAfterDraw)

                val newBoard = board.play(position, Play(action.position, card)).orRaiseError()
                copy(moveFrom = position, players = newPlayers, board = newBoard, previous = copy(action = action))
            }
        }
    }

    fun getState(): State =
        if (previous?.action == Skip && previous.previous?.action == Skip) {
            Tie
        } else {
            Ongoing
        }
}
