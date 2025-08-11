package br.com.gabryel.reginaesanguine.domain

import arrow.core.fold
import br.com.gabryel.reginaesanguine.domain.effect.DestroyCards
import br.com.gabryel.reginaesanguine.domain.effect.Effect
import br.com.gabryel.reginaesanguine.domain.effect.EffectWithAffected
import br.com.gabryel.reginaesanguine.domain.effect.GameSummarizer
import br.com.gabryel.reginaesanguine.domain.effect.None
import br.com.gabryel.reginaesanguine.domain.effect.Raisable
import br.com.gabryel.reginaesanguine.domain.effect.Scoped
import br.com.gabryel.reginaesanguine.domain.effect.Trigger
import br.com.gabryel.reginaesanguine.domain.effect.WhenDestroyed
import br.com.gabryel.reginaesanguine.domain.effect.WhenPlayed
import br.com.gabryel.reginaesanguine.domain.effect.WhileActive
import kotlin.collections.fold
import kotlin.reflect.KClass

data class EffectSource(val player: PlayerPosition, val effect: Effect, val position: Position)

data class EffectRegistry(
    private val triggered: Map<Position, List<EffectSource>> = emptyMap(),
    private val byTrigger: Map<KClass<out Trigger>, Map<Position, EffectSource>> = emptyMap()
) {
    private val activeEffects = gatherActiveEffects()

    fun onPlaceCard(player: PlayerPosition, effect: Effect, position: Position, board: CellContainer): EffectRegistry =
        addTrigger(effect, position, player)
            .triggerWhenPlayed(board, position)

    fun onDestroy(positions: Set<Position>, board: CellContainer): EffectRegistry =
        triggerWhenDestroyed(positions, board)
            .removeTriggered(positions)
            .removeTrigger(positions)

    fun getExtraPowerAt(position: Position, board: CellContainer): Int = activeEffects.getExtraPower(board, position)

    fun getDestroyable(board: CellContainer): Set<Position> = activeEffects.filter { (position, effects) ->
        val cell = board.getCellAt(position).orNull() ?: return@filter false
        val card = cell.card ?: return@filter false
        val owner = cell.owner ?: return@filter false

        if (effects.any { it.effect is DestroyCards && it.effect.target.isTargetable(it.player, owner, false) })
            return@filter true

        card.power - getExtraPowerAt(position, board) > 0
    }.keys

    private fun removeTriggered(positions: Set<Position>): EffectRegistry =
        copy(triggered = triggered - positions)

    private fun removeTrigger(positions: Set<Position>): EffectRegistry =
        copy(byTrigger = byTrigger.mapValues { (_, byTriggerTpe) -> byTriggerTpe - positions })

    private fun triggerWhenPlayed(board: CellContainer, position: Position): EffectRegistry {
        val cells = listOfNotNull(board.getCellAt(position).map { position to it }.orNull())
        return triggerScoped<WhenPlayed>(cells, board)
    }

    private fun triggerWhenDestroyed(positions: Set<Position>, board: CellContainer): EffectRegistry {
        val cells = positions.mapNotNull { position ->
            board.getCellAt(position).map { position to it }.orNull()
        }

        return triggerScoped<WhenDestroyed>(cells, board)
    }

    private inline fun <reified T> triggerScoped(cells: List<Pair<Position, Cell>>, board: CellContainer): EffectRegistry
    where T : Trigger, T : Scoped {
        val scopedTriggers = byTrigger[T::class].orEmpty()

        return scopedTriggers.fold(this) { accRegistry, (_, sourceEffect) ->
            val trigger = sourceEffect.effect.trigger as? T ?: return accRegistry

            cells.fold(accRegistry) { accRegistry, (targetPosition, targetCell) ->
                targetCell.card ?: return@fold accRegistry
                val targetPlayer = targetCell.owner ?: return@fold accRegistry
                val self = targetPosition == sourceEffect.position
                if (!trigger.isInScope(sourceEffect.player, targetPlayer, self))
                    return@fold accRegistry

                accRegistry.applyEffect(sourceEffect, board)
            }
        }
    }

    private fun applyEffect(sourceEffect: EffectSource, board: CellContainer): EffectRegistry {
        val newTriggered = sourceEffect.getAffected()
            .filterValidCards(board)
            .fold(triggered) { acc, position -> acc + (position to (acc[position].orEmpty() + sourceEffect)) }

        return copy(triggered = newTriggered)
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

    private fun Map<Position, List<EffectSource>>.getExtraPower(board: CellContainer, position: Position): Int {
        val summarizer = GameSummarizer.forBoard(board, this@EffectRegistry)

        return this[position].orEmpty().sumOf { sourceEffect ->
            if (sourceEffect.effect !is Raisable) return@sumOf 0
            val targeted = board.getCellAt(position).orNull()?.owner ?: return@sumOf 0

            val self = sourceEffect.position == position
            sourceEffect.effect
                .getRaiseBy(summarizer, sourceEffect.player, targeted, sourceEffect.position, self)
        }
    }

    private fun EffectSource.getAffected(): List<Position> {
        if (effect !is EffectWithAffected)
            return emptyList()

        return effect.getAffectedPositions(position, player)
    }

    private fun gatherActiveEffects(): Map<Position, List<EffectSource>> {
        val triggered = triggered.flatMap { (_, effectSources) ->
            effectSources.flatMap { effectSource -> effectSource.getAffected().map { it to effectSource } }
        }

        val whileActive = byTrigger[WhileActive::class].orEmpty()
            .flatMap { (_, effectSource) -> effectSource.getAffected().map { it to effectSource } }

        return (triggered + whileActive).groupBy({ it.first }) { it.second }
    }
}
