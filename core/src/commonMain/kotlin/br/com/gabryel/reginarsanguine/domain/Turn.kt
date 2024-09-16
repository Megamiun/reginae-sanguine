package br.com.gabryel.reginarsanguine.domain

import arrow.core.raise.ensure
import br.com.gabryel.reginarsanguine.domain.Failure.NotPlayerTurn
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginarsanguine.util.buildResult

data class Turn(
    private val player: PlayerPosition,
    private val board: Board
) {
    val nextPlayer = when (player) {
        LEFT -> RIGHT
        RIGHT -> LEFT
    }

    companion object {
        fun default() = Turn(RIGHT, Board.default())
    }

    fun play(player: PlayerPosition, action: Action): Result<Turn> = buildResult {
        ensure(nextPlayer == player) { NotPlayerTurn(this@Turn) }

        val newBoard = board.play(player, action).orRaiseError()
        copy(player = player, board = newBoard)
    }

    fun getCellAt(row: Int, column: Int): Result<Cell> = board.getCellAt(row, column)
}