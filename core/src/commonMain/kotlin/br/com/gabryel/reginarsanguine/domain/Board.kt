package br.com.gabryel.reginarsanguine.domain

import arrow.core.filterIsInstance
import arrow.core.raise.ensure
import br.com.gabryel.reginarsanguine.domain.Action.Play
import br.com.gabryel.reginarsanguine.domain.Failure.*
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginarsanguine.util.buildResult

data class Board(
    private val width: Int,
    private val height: Int,
    private val state: Map<Position, Cell> =
        mapOf(
            (0 to 0) to Cell(LEFT, 1),
            (1 to 0) to Cell(LEFT, 1),
            (2 to 0) to Cell(LEFT, 1),
            (0 to 4) to Cell(RIGHT, 1),
            (1 to 4) to Cell(RIGHT, 1),
            (2 to 4) to Cell(RIGHT, 1),
        ),
) : CellContainer {
    companion object {
        fun default() = Board(5, 3)
    }

    fun play(
        player: PlayerPosition,
        action: Play<Card>,
    ) = buildResult {
        val cell = getCellAt(action.position).orRaiseError()

        ensure(cell.owner == player) { CellDoesNotBelongToPlayer(cell) }
        ensure(cell.pins >= action.card.cost) { NotEnoughPins(cell) }
        ensure(cell.card == null) { CellOccupied(cell) }

        val newState = state +
                (action.position to cell.copy(owner = player, card = action.card)) +
                action.incrementAll(player)

        val afterEffectsState = addEffects(action, player, newState)

        copy(state = afterEffectsState)
    }

    private fun addEffects(action: Play<Card>, player: PlayerPosition, newState: Map<Position, Cell>) =
        action.card.effects.fold(newState) { newState, effect ->
            newState + effect.relativePosition.mapNotNull { displacement ->
                val effectPosition = action.position + displacement

                newState.getCellAt(effectPosition)
                    .map { cell ->
                        val modifiedCell = cell.copy(appliedEffects = cell.appliedEffects + listOf(player to effect))
                        effectPosition to modifiedCell
                    }
                    .orNull()
            }
        }

    fun getScores(): Map<PlayerPosition, Int> = (0..2)
        .map(::getRowScore)
        .fold(mapOf(), ::addScore)

    override fun getCellAt(position: Position) = state.getCellAt(position)

    private fun Map<Position, Cell>.getCellAt(position: Position): Result<Cell> = buildResult {
        ensure(position.lane() in 0 until height && position.column() in 0 until width) { OutOfBoard(position) }

        this@getCellAt[position] ?: Cell.EMPTY
    }

    private fun getRowScore(row: Int): Pair<PlayerPosition, Int> = PlayerPosition.entries
        .associateWith { player ->
            state.entries
                .filter { it.value.owner == player && it.key.lane() == row }
                .mapNotNull { it.value.card?.power }
                .sum()
        }.maxBy { it.value }
        .toPair()

    private fun addScore(
        acc: Map<PlayerPosition, Int>,
        curr: Pair<PlayerPosition, Int>,
    ) = acc + (curr.first to (curr.second + (acc[curr.first] ?: 0)))

    private fun Play<Card>.incrementAll(player: PlayerPosition) =
        card.increments
            .mapKeys { (displacement) -> position + displacement }
            .mapValues { (newPosition, increment) ->
                getCellAt(newPosition).map { it.increment(player, increment) }
            }.filterIsInstance<Position, Success<Cell>>()
            .mapValues { (_, newCell) -> newCell.value }
}
