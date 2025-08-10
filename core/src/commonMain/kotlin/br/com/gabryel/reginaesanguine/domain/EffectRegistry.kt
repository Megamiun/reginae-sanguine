package br.com.gabryel.reginaesanguine.domain

import br.com.gabryel.reginaesanguine.domain.effect.DestroyCards
import br.com.gabryel.reginaesanguine.domain.effect.Effect
import br.com.gabryel.reginaesanguine.domain.effect.EffectWithAffected
import br.com.gabryel.reginaesanguine.domain.effect.None
import br.com.gabryel.reginaesanguine.domain.effect.Raisable
import br.com.gabryel.reginaesanguine.domain.effect.Trigger
import br.com.gabryel.reginaesanguine.domain.effect.WhenDestroyed
import br.com.gabryel.reginaesanguine.domain.effect.WhenPlayed
import br.com.gabryel.reginaesanguine.domain.effect.WhileActive
import kotlin.reflect.KClass

data class EffectSource(val player: PlayerPosition, val effect: Effect, val position: Position)

data class EffectRegistry(
    private val triggered: Map<Position, List<EffectSource>> = emptyMap(),
    private val byTrigger: Map<KClass<out Trigger>, Map<Position, EffectSource>> = emptyMap()
) {
    val whileActiveEffects = byTrigger[WhileActive::class].orEmpty().flatMap { (_, effectSource) ->
        effectSource.getAffected().map { it to effectSource }
    }.groupBy({ it.first }) { it.second }

    fun onPlaceCard(player: PlayerPosition, effect: Effect?, position: Position, board: Board): EffectRegistry {
        if (effect == null || effect.trigger == None)
            return this

        if (effect.trigger is WhenPlayed)
            return runPermanentEffect(EffectSource(player, effect, position), board)

        return listenToTrigger(effect, position, player)
    }

    fun onDestroy(positions: Set<Position>, board: Board): EffectRegistry =
        removeTriggered(positions)
            .removeByTrigger(positions)
            .triggerWhenDestroyed(positions, board)

    private fun removeTriggered(positions: Set<Position>): EffectRegistry =
        copy(triggered = triggered - positions)

    private fun removeByTrigger(positions: Set<Position>): EffectRegistry =
        copy(byTrigger = byTrigger.mapValues { (_, byTriggerTpe) -> byTriggerTpe - positions })

    private fun triggerWhenDestroyed(positions: Set<Position>, board: Board): EffectRegistry {
        val onDestroy = byTrigger[WhenDestroyed::class].orEmpty()

        return positions.fold(this) { acc, position ->
            val playerEffect = onDestroy[position] ?: return@fold acc

            acc.runPermanentEffect(playerEffect, board)
        }
    }

    fun getExtraPowerAt(position: Position, board: Board): Int =
        whileActiveEffects.getExtraPower(board, position) + triggered.getExtraPower(board, position)

    fun getDestroyable(board: Board): Set<Position> = triggered.filter { (position, effects) ->
        val cell = board.getCellAt(position).orNull() ?: return@filter false
        val card = cell.card ?: return@filter false
        val owner = cell.owner ?: return@filter false

        if (effects.any { it.effect is DestroyCards && it.effect.target.isTargetable(it.player, owner) })
            return@filter true

        card.power - getExtraPowerAt(position, board) > 0
    }.keys

    private fun runPermanentEffect(sourceEffect: EffectSource, board: Board): EffectRegistry {
        val newTriggered = sourceEffect.getAffected()
            .filterValidCards(board)
            .fold(triggered) { acc, position -> acc + (position to (acc[position].orEmpty() + sourceEffect)) }

        return copy(triggered = newTriggered)
    }

    private fun List<Position>.filterValidCards(board: Board) =
        filter { board.getCellAt(it).orNull()?.card != null }

    private fun listenToTrigger(effect: Effect, position: Position, player: PlayerPosition): EffectRegistry {
        val triggerType = effect.trigger::class

        val newEffect = position to EffectSource(player, effect, position)
        val effectsOnTrigger = byTrigger.getOrElse(triggerType) { emptyMap() } + newEffect

        return copy(byTrigger = byTrigger + (triggerType to effectsOnTrigger))
    }

    private fun Map<Position, List<EffectSource>>.getExtraPower(board: Board, position: Position): Int =
        this[position].orEmpty().sumOf { sourceEffect ->
            if (sourceEffect.effect !is Raisable) return@sumOf 0
            val targeted = board.getCellAt(position).orNull()?.owner ?: return@sumOf 0

            sourceEffect.effect.getRaiseBy(board, this@EffectRegistry, sourceEffect.player, targeted, sourceEffect.position)
        }

    private fun EffectSource.getAffected(): List<Position> {
        if (effect !is EffectWithAffected)
            return emptyList()

        return effect.getAffectedPositions(position, player)
    }
}
