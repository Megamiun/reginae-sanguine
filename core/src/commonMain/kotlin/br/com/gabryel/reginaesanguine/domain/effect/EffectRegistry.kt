package br.com.gabryel.reginaesanguine.domain.effect

import arrow.core.fold
import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.CellContainer
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.effect.type.DestroyCards
import br.com.gabryel.reginaesanguine.domain.effect.type.Effect
import br.com.gabryel.reginaesanguine.domain.effect.type.EffectWithAffected
import br.com.gabryel.reginaesanguine.domain.effect.type.LaneBonus
import br.com.gabryel.reginaesanguine.domain.effect.type.PlayerEffect
import br.com.gabryel.reginaesanguine.domain.effect.type.PlayerModification
import br.com.gabryel.reginaesanguine.domain.effect.type.Raisable
import kotlin.collections.fold
import kotlin.collections.orEmpty
import kotlin.collections.plus
import kotlin.reflect.KClass

data class EffectSource<T : Effect>(val player: PlayerPosition, val effect: T, val position: Position) {
    inline fun <reified V : T> upcastOrNull(): EffectSource<V>? =
        if (effect is V) EffectSource(player, effect, position) else null
}

data class EffectApplicationResult(
    val effectRegistry: EffectRegistry,
    val playerModifications: Map<PlayerPosition, PlayerModification> = emptyMap(),
    val toDelete: Set<Position> = emptySet()
)

data class EffectRegistry(
    private val appliedRaises: Map<Position, List<EffectSource<Raisable>>> = emptyMap(),
    private val byTrigger: Map<KClass<out Trigger>, Map<Position, EffectSource<Effect>>> = emptyMap()
) {
    private val activeEffects = gatherActiveEffects()

    fun onPlaceCard(player: PlayerPosition, effect: Effect, position: Position, board: CellContainer): EffectApplicationResult =
        addTrigger(effect, position, player)
            .triggerScoped<WhenPlayed>(listOf(position to board.getCellAt(position).orNull()!!), board)

    fun onDestroy(positions: Set<Position>, board: CellContainer): EffectApplicationResult =
        triggerWhenDestroyed(positions, board).let { result ->
            val cleanRegistry = result.effectRegistry
                .removeTriggered(positions)
                .removeTrigger(positions)

            result.copy(effectRegistry = cleanRegistry)
        }

    fun getExtraPowerAtLane(lane: Int, board: CellContainer): Map<PlayerPosition, Int> {
        val summarizer = GameSummarizer.forBoard(board, this)

        return byTrigger[WhenLaneWon::class].orEmpty().map { (position, sourceEffect) ->
            when (val effect = sourceEffect.effect) {
                is LaneBonus ->
                    effect.getRaiseLaneAmounts(summarizer, sourceEffect.player, position)[lane]
                        ?: emptyMap()
                else -> emptyMap()
            }
        }.fold(emptyMap(), ::accumulateScore)
    }

    fun getExtraPowerAt(position: Position, board: CellContainer): Int {
        val summarizer = GameSummarizer.forBoard(board, this)
        return activeEffects[position].orEmpty().sumOf { sourceEffect ->
            val targeted = board.getCellAt(position).orNull()?.owner ?: return@sumOf 0

            val self = sourceEffect.position == position
            sourceEffect.effect
                .getRaiseBy(summarizer, sourceEffect.player, targeted, sourceEffect.position, self)
        }
    }

    fun getDestroyable(board: CellContainer): Set<Position> = activeEffects.filter { (position, effects) ->
        val cell = board.getCellAt(position).orNull() ?: return@filter false
        val card = cell.card ?: return@filter false

        card.power - getExtraPowerAt(position, board) > 0
    }.keys

    private fun removeTriggered(positions: Set<Position>): EffectRegistry =
        copy(appliedRaises = appliedRaises - positions)

    private fun removeTrigger(positions: Set<Position>): EffectRegistry =
        copy(byTrigger = byTrigger.mapValues { (_, byTriggerTpe) -> byTriggerTpe - positions })

    private fun triggerWhenDestroyed(positions: Set<Position>, board: CellContainer): EffectApplicationResult {
        val cells = positions.mapNotNull { position ->
            board.getCellAt(position).map { position to it }.orNull()
        }

        return triggerScoped<WhenDestroyed>(cells, board)
    }

    private inline fun <reified T> triggerScoped(cells: List<Pair<Position, Cell>>, board: CellContainer): EffectApplicationResult
    where T : Trigger, T : Scoped {
        val scopedTriggers = byTrigger[T::class].orEmpty()

        return scopedTriggers.fold(EffectApplicationResult(this)) { accResult, (_, sourceEffect) ->
            val effect = sourceEffect.effect
            val trigger = effect.trigger as? T ?: return accResult

            val cells = cells.filter { cell -> sourceEffect.isInScope(cell, trigger) }
            if (cells.isEmpty()) return@fold accResult

            when (effect) {
                is Raisable -> {
                    val newRegistry = cells.fold(accResult.effectRegistry) { accRegistry, _ ->
                        accRegistry.applyRaise(sourceEffect.upcastOrNull<Raisable>()!!, board)
                    }
                    accResult.copy(effectRegistry = newRegistry)
                }
                is PlayerEffect -> {
                    val summarizer = GameSummarizer.forBoard(board, accResult.effectRegistry)
                    val modifications = cells.fold(accResult.playerModifications) { accModifications, _ ->
                        accModifications + effect.getPlayerModifications(summarizer, sourceEffect.player, sourceEffect.position)
                    }
                    accResult.copy(playerModifications = modifications)
                }
                is DestroyCards -> {
                    val toDelete = effect.getAffectedPositions(sourceEffect.position, sourceEffect.player)
                    accResult.copy(toDelete = accResult.toDelete + toDelete)
                }
                else -> accResult
            }
        }
    }

    private fun EffectSource<*>.isInScope(cell: Pair<Position, Cell>, trigger: Scoped): Boolean {
        cell.second.card ?: return false
        val targetPlayer = cell.second.owner ?: return false

        return trigger.isInScope(player, targetPlayer, cell.first == position)
    }

    private fun applyRaise(sourceEffect: EffectSource<Raisable>, board: CellContainer): EffectRegistry {
        val newTriggered = sourceEffect.getAffected()
            .filterValidCards(board)
            .fold(appliedRaises) { acc, position -> acc + (position to (acc[position].orEmpty() + sourceEffect)) }

        return copy(appliedRaises = newTriggered)
    }

    private fun List<Position>.filterValidCards(board: CellContainer) =
        filter { board.getCellAt(it).orNull()?.card != null }

    private fun addTrigger(effect: Effect, position: Position, player: PlayerPosition): EffectRegistry {
        if (effect.trigger == None)
            return this

        val triggerType = effect.trigger::class

        val newEffect = position to EffectSource(player, effect, position)
        val effectsOnTrigger = byTrigger.getOrElse(triggerType) { emptyMap() } + newEffect

        return copy(byTrigger = byTrigger + (triggerType to effectsOnTrigger))
    }

    private fun gatherActiveEffects(): Map<Position, List<EffectSource<Raisable>>> {
        val triggered = appliedRaises.flatMap { (_, effectSources) ->
            effectSources.flatMap { effectSource -> effectSource.getAffected().map { it to effectSource } }
        }

        val whileActive = byTrigger[WhileActive::class].orEmpty()
            .mapNotNull { (l, r) -> r.upcastOrNull<Raisable>()?.let { l to it } }
            .flatMap { (_, effectSource) -> effectSource.getAffected().map { it to effectSource } }

        return (triggered + whileActive).groupBy({ it.first }) { it.second }
    }

    private fun EffectSource<*>.getAffected(): List<Position> {
        if (effect !is EffectWithAffected)
            return emptyList()

        return effect.getAffectedPositions(position, player)
    }

    private fun accumulateScore(acc: Map<PlayerPosition, Int>, curr: Map<PlayerPosition, Int>) =
        PlayerPosition.entries.associateWith { position ->
            (acc[position] ?: 0) + (curr[position] ?: 0)
        }
}
