package br.com.gabryel.reginaesanguine.domain

import arrow.core.raise.ensure
import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.Failure.CellDoesNotBelongToPlayer
import br.com.gabryel.reginaesanguine.domain.Failure.CellOccupied
import br.com.gabryel.reginaesanguine.domain.Failure.CellOutOfBoard
import br.com.gabryel.reginaesanguine.domain.Failure.CellRankLowerThanCard
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.effect.EffectApplicationResult
import br.com.gabryel.reginaesanguine.domain.effect.EffectRegistry
import br.com.gabryel.reginaesanguine.domain.effect.type.PlayerModification
import br.com.gabryel.reginaesanguine.domain.util.buildResult

data class BoardWithEffectApplication(
    val board: Board,
    val playerModifications: Map<PlayerPosition, PlayerModification> = emptyMap()
)

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

    fun play(player: PlayerPosition, action: Play<Card>): Result<BoardWithEffectApplication> =
        buildResult {
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

    override fun getTotalScoreAt(position: Position): Result<Int> = buildResult {
        ensure(position in size) { CellOutOfBoard(position) }
        getTotalPowerAt(position)
    }

    override fun getBaseLaneScoreAt(lane: Int): Map<PlayerPosition, Int> =
        (0..size.width)
            .groupBy({ column -> getCellAt(lane atColumn column).orNull()?.owner }) { column ->
                getTotalPowerAt(lane atColumn column)
            }.mapValues { (_, values) -> values.sum() }
            .filterKeys { it != null } as Map<PlayerPosition, Int>

    override fun getExtraLaneScoreAt(lane: Int): Map<PlayerPosition, Int> =
        effectRegistry.getExtraPowerAtLane(lane, this)

    override fun getOccupiedCells() = state.filter { it.value.card != null }

    private fun placeCard(
        position: Position,
        cell: Cell,
        player: PlayerPosition,
        card: Card
    ): BoardWithEffectApplication {
        val incremented = card.increments.mapNotNull { displacement ->
            val newPosition = position + player.correct(displacement)
            getCellAt(newPosition).orNull()
                ?.takeIf { it.owner != player.opponent && it.card == null }
                ?.let { cell -> newPosition to cell.increment(player, card.incrementValue) }
        }

        val newCellCard = (position to cell.copy(card = card))
        val newBoard = copy(state = state + incremented + newCellCard)

        val effectApplicationResult = effectRegistry.onPlaceCard(player, card.effect, position, newBoard)

        return copy(state = newBoard.state, effectRegistry = effectApplicationResult.effectRegistry)
            .resolveEffects(effectApplicationResult)
    }

    private fun resolveEffects(result: EffectApplicationResult): BoardWithEffectApplication {
        val (newBoard, newResult) = destroy(result)

        if (newBoard == this && result == newResult)
            return BoardWithEffectApplication(newBoard, newResult.playerModifications)

        return newBoard.resolveEffects(newResult)
    }

    private fun destroy(result: EffectApplicationResult): Pair<Board, EffectApplicationResult> {
        val effectRegistry = result.effectRegistry
        val toDelete = result.toDelete + effectRegistry.getDestroyable(this)
        val newState = toDelete.fold(state) { acc, position ->
            val originalCell = acc[position] ?: return@fold acc

            // TODO Maybe we need to change cell ownership
            acc + (position to originalCell.copy(card = null))
        }

        val newBoard = copy(state = newState)
        val afterDestroyResult = effectRegistry
            .onDestroy(toDelete, newBoard)
            .addPlayerModificationsFrom(result)
        return newBoard.copy(effectRegistry = afterDestroyResult.effectRegistry) to afterDestroyResult
    }

    private fun EffectApplicationResult.addPlayerModificationsFrom(previousResult: EffectApplicationResult): EffectApplicationResult {
        val newPlayerModifications = PlayerPosition.entries.associateWith {
            val previous = playerModifications[it]?.cardsToAdd.orEmpty()
            val current = previousResult.playerModifications[it]?.cardsToAdd.orEmpty()

            PlayerModification(previous + current)
        }

        return copy(playerModifications = newPlayerModifications)
    }

    private fun getTotalPowerAt(position: Position): Int {
        val cell = getCellAt(position).orNull() ?: return 0
        val card = cell.card ?: return 0

        return card.power + effectRegistry.getExtraPowerAt(position, this)
    }

    private fun getWinLaneScore(lane: Int): Pair<PlayerPosition, Int>? {
        val score = getBaseLaneScoreAt(lane)

        val maxValue = score.maxOf { it.value }
        val winners = score.filter { it.value == maxValue }.map { it.key }

        if (winners.size != 1) return null

        val extra = getExtraLaneScoreAt(lane)

        val winner = winners.first()
        return winner to ((score[winner] ?: 0) + (extra[winner] ?: 0))
    }

    private fun accumulateScore(
        acc: Map<PlayerPosition, Int>,
        curr: Pair<PlayerPosition, Int>,
    ) = acc + (curr.first to (curr.second + (acc[curr.first] ?: 0)))
}
