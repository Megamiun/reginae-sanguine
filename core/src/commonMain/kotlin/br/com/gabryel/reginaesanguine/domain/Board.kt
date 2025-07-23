package br.com.gabryel.reginaesanguine.domain

import arrow.core.raise.ensure
import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.Failure.CellDoesNotBelongToPlayer
import br.com.gabryel.reginaesanguine.domain.Failure.CellOccupied
import br.com.gabryel.reginaesanguine.domain.Failure.NotEnoughPins
import br.com.gabryel.reginaesanguine.domain.Failure.OutOfBoard
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.util.buildResult

data class Board(
    private val state: Map<Position, Cell> = createInitialState(),
    override val width: Int = 5,
    override val height: Int = 3,
) : CellContainer {
    companion object {
        fun default() = Board()

        private fun createInitialState(): Map<Position, Cell> = mapOf(
            // Left player starting positions
            (0 to 0) to Cell(LEFT, 1),
            (1 to 0) to Cell(LEFT, 1),
            (2 to 0) to Cell(LEFT, 1),
            // Right player starting positions
            (0 to 4) to Cell(RIGHT, 1),
            (1 to 4) to Cell(RIGHT, 1),
            (2 to 4) to Cell(RIGHT, 1),
        )
    }

    fun play(
        player: PlayerPosition,
        action: Play<Card>,
    ) = buildResult {
        val cell = getCellAt(action.position).orRaiseError()

        ensure(cell.owner == player) { CellDoesNotBelongToPlayer(cell) }
        ensure(cell.pins >= action.card.cost) { NotEnoughPins(cell) }
        ensure(cell.card == null) { CellOccupied(cell) }

        placeCard(action.position, cell, player, action.card)
            .applyCardIncrements(action, player)
            .applyCardEffects(action, player)
    }

    fun getScores(): Map<PlayerPosition, Int> = (0..2)
        .mapNotNull(::getWinLaneScore)
        .fold(mapOf(LEFT to 0, RIGHT to 0), ::accumulateScore)

    override fun getCellAt(position: Position): Result<Cell> = buildResult {
        ensure(position.lane() in 0 until height && position.column() in 0 until width) { OutOfBoard(position) }
        state[position] ?: Cell.EMPTY
    }

    private fun placeCard(position: Position, cell: Cell, player: PlayerPosition, card: Card): Board =
        copy(state = state + (position to cell.copy(owner = player, card = card)))

    private fun applyCardIncrements(
        action: Play<Card>,
        player: PlayerPosition,
    ): Board {
        val affectedCells = action.card.increments
            .mapKeys { (displacement) -> action.position + player.correct(displacement) }
            .mapNotNull { (newPosition, increment) ->
                getCellAt(newPosition)
                    .map { cell -> newPosition to cell.increment(player, increment) }
                    .orNull()
            }

        return copy(state = state + affectedCells)
    }

    private fun applyCardEffects(action: Play<Card>, player: PlayerPosition): Board {
        val affectedCards = action.card.effectDisplacements.mapNotNull { displacement ->
            val effectPosition = action.position + player.correct(displacement)
            getCellAt(effectPosition)
                .map { cell -> effectPosition to cell.applyEffects(action.card.effects, player) }
                .orNull()
        }

        return copy(state = state + affectedCards)
    }

    private fun Cell.applyEffects(effects: List<Effect>, player: PlayerPosition): Cell {
        val hasDestroy = effects.any { it is DestroyEntity }
        val newCard = if (hasDestroy && card != null && owner != player) null else card

        return copy(
            card = newCard,
            appliedEffects = appliedEffects + effects.map { player to it },
        )
    }

    private fun getWinLaneScore(lane: Int): Pair<PlayerPosition, Int>? {
        val score = getLaneScores(lane)
        val winner = score.maxBy { it.value }

        if (score.values.all { it == winner.value }) return null

        return winner.toPair()
    }

    fun getLaneScores(lane: Int): Map<PlayerPosition, Int> {
        val basePowers = PlayerPosition.entries.associateWith { player ->
            getPlayerCardsInLane(player, lane).sumOf { it.power }
        }

        val winner = basePowers.maxBy { it.value }

        if (basePowers.values.all { it == winner.value }) return basePowers

        val laneBonus = getPlayerCardsInLane(winner.key, lane)
            .flatMap { it.effects }
            .mapNotNull { it as? WinLaneBonusPoints }
            .sumOf { it.bonusPoints }

        return basePowers + (winner.key to (winner.value + laneBonus))
    }

    private fun getPlayerCardsInLane(player: PlayerPosition, lane: Int): List<Card> =
        state.entries
            .filter { it.value.owner == player && it.key.lane() == lane }
            .mapNotNull { it.value.card }

    private fun accumulateScore(
        acc: Map<PlayerPosition, Int>,
        curr: Pair<PlayerPosition, Int>,
    ) = acc + (curr.first to (curr.second + (acc[curr.first] ?: 0)))
}
