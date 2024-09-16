package br.com.gabryel.reginarsanguine.domain

import arrow.core.raise.ensure
import br.com.gabryel.reginarsanguine.domain.Failure.*
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginarsanguine.util.buildResult

data class Board(
    private val width: Int,
    private val height: Int,
    private val state: Map<Pair<Int, Int>, Cell> = mapOf(
        (0 to 0) to Cell(LEFT, 1),
        (1 to 0) to Cell(LEFT, 1),
        (2 to 0) to Cell(LEFT, 1),
        (0 to 4) to Cell(RIGHT, 1),
        (1 to 4) to Cell(RIGHT, 1),
        (2 to 4) to Cell(RIGHT, 1),
    )
) {

    companion object {
        fun default() = Board(5, 3)
    }

    fun play(player: PlayerPosition, action: Action) = buildResult {
        when (action) {
            is Action.Skip -> this@Board
            is Action.Play -> {
                val cell = getCellAt(action.row, action.column).orRaiseError()

                ensure(cell.owner == player) { DoesNotBelongToPlayer(cell) }
                ensure(cell.pins >= action.card.price) { NotEnoughPins(cell) }

                val newCell = cell.copy(owner = player, card = action.card)
                copy(state = state + (action.row to action.column to newCell))
            }
        }
    }

    fun getCellAt(row: Int, column: Int) = buildResult {
        ensure(row in 0 until height && column in 0 until width) { CellOutsideOfBoard(row, column) }

        state[row to column] ?: Cell.EMPTY
    }
}