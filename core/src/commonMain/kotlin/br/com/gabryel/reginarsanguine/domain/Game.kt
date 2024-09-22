package br.com.gabryel.reginarsanguine.domain

import arrow.core.raise.ensure
import br.com.gabryel.reginarsanguine.domain.Failure.NotPlayerTurn
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginarsanguine.util.buildResult

data class Game(
    private val player: PlayerPosition,
    private val board: Board,
    val action: Action? = null,
    val previous: Game? = null,
) : CellContainer by board {
    val nextPlayer =
        when (player) {
            LEFT -> RIGHT
            RIGHT -> LEFT
        }

    companion object {
        fun default() = Game(RIGHT, Board.default())
    }

    fun play(
        player: PlayerPosition,
        action: Action,
    ): Result<Game> =
        buildResult {
            ensure(nextPlayer == player) { NotPlayerTurn(this@Game) }

            val newBoard = board.play(player, action).orRaiseError()
            copy(player = player, board = newBoard, previous = copy(action = action))
        }
}
