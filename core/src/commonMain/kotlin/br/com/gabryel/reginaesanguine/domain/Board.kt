package br.com.gabryel.reginaesanguine.domain

import arrow.core.raise.ensure
import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.Failure.CellDoesNotBelongToPlayer
import br.com.gabryel.reginaesanguine.domain.Failure.CellOccupied
import br.com.gabryel.reginaesanguine.domain.Failure.CellOutOfBoard
import br.com.gabryel.reginaesanguine.domain.Failure.CellRankLowerThanCard
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.effect.ScoreBonus
import br.com.gabryel.reginaesanguine.domain.effect.WhenLaneWon
import br.com.gabryel.reginaesanguine.domain.util.buildResult

val DEFAULT_BOARD_SIZE = Size(5, 3)

data class Board(
    private val state: Map<Position, Cell> = createInitialState(),
    private val effectRegistry: EffectRegistry = EffectRegistry(),
    override val size: Size = DEFAULT_BOARD_SIZE
) : CellContainer {
    companion object {
        fun default() = Board()

        private fun createInitialState(): Map<Position, Cell> = mapOf(
            // Left player starting positions
            (0 atColumn 0) to Cell(LEFT, 1),
            (1 atColumn 0) to Cell(LEFT, 1),
            (2 atColumn 0) to Cell(LEFT, 1),
            // Right player starting positions
            (0 atColumn 4) to Cell(RIGHT, 1),
            (1 atColumn 4) to Cell(RIGHT, 1),
            (2 atColumn 4) to Cell(RIGHT, 1),
        )
    }

    fun play(player: PlayerPosition, action: Play<Card>) = buildResult {
        val cell = getCellAt(action.position).orRaiseError()

        ensure(cell.owner == player) { CellDoesNotBelongToPlayer(cell) }
        ensure(cell.rank >= action.card.rank) { CellRankLowerThanCard(cell) }
        ensure(cell.card == null) { CellOccupied(cell) }

        placeCard(action.position, cell, player, action.card)
    }

    fun getScores(): Map<PlayerPosition, Int> = (0..2)
        .mapNotNull(::getWinLaneScore)
        .fold(mapOf(LEFT to 0, RIGHT to 0), ::accumulateScore)

    override fun getCellAt(position: Position): Result<Cell> = buildResult {
        ensure(position in size) { CellOutOfBoard(position) }
        state[position] ?: Cell.EMPTY
    }

    override fun getScoreAt(position: Position): Result<Int> = buildResult {
        ensure(position in size) { CellOutOfBoard(position) }
        getTotalPowerAt(position)
    }

    private fun placeCard(position: Position, cell: Cell, player: PlayerPosition, card: Card): Board {
        val incremented = card.increments.mapNotNull { displacement ->
            val newPosition = position + player.correct(displacement)
            getCellAt(newPosition).orNull()
                ?.takeIf { it.owner != player.opponent && it.card == null }
                ?.let { cell -> newPosition to cell.increment(player, card.incrementValue) }
        }

        val newCellCard = (position to cell.copy(card = card))
        val newState = state + incremented + newCellCard

        val newEffectRegistry = effectRegistry.onPlaceCard(player, card.effect, position, this)

        return copy(state = newState, effectRegistry = newEffectRegistry)
            .resolveEffects()
    }

    private fun resolveEffects(): Board {
        val newState = destroy()

        if (newState == this) return this

        return newState.resolveEffects()
    }

    fun getLaneScores(lane: Int): Map<PlayerPosition, Int> {
        val basePowers = PlayerPosition.entries.associateWith { player ->
            getPlayerPositionsInLane(player, lane)
                .sumOf { position -> getTotalPowerAt(position) }
        }

        val winner = basePowers.maxBy { it.value }

        if (basePowers.values.all { it == winner.value }) return basePowers

        val laneBonus = getPlayerPositionsInLane(winner.key, lane)
            .mapNotNull { getCellAt(it).orNull()?.card?.effect as? ScoreBonus }
            .sumOf { it.amount }

        return basePowers + (winner.key to (winner.value + laneBonus))
    }

    private fun destroy(): Board {
        val destroyable = effectRegistry.getDestroyable(this)

        val newState = destroyable.fold(state) { acc, position ->
            val originalCell = acc[position] ?: return@fold acc

            // TODO Maybe we need to change cell ownership
            acc + (position to originalCell.copy(card = null))
        }

        val newBoard = copy(state = newState)

        val newRegistry = effectRegistry.onDestroy(destroyable, newBoard)

        return newBoard.copy(effectRegistry = newRegistry)
    }

    private fun getTotalPowerAt(position: Position): Int {
        val cell = getCellAt(position).orNull() ?: return 0
        val card = cell.card ?: return 0

        return card.power + effectRegistry.getExtraPowerAt(position, this)
    }

    private fun getWinLaneScore(lane: Int): Pair<PlayerPosition, Int>? {
        val score = getLaneScores(lane)
        val winner = score.maxBy { it.value }

        if (score.values.all { it == winner.value }) return null

        return winner.toPair()
    }

    private fun getPlayerPositionsInLane(player: PlayerPosition, lane: Int): List<Position> =
        state.entries
            .filter { it.value.owner == player && it.key.lane == lane }
            .map { it.key }

    private fun accumulateScore(
        acc: Map<PlayerPosition, Int>,
        curr: Pair<PlayerPosition, Int>,
    ) = acc + (curr.first to (curr.second + (acc[curr.first] ?: 0)))
}
