package br.com.gabryel.reginaesanguine.domain

import arrow.core.fold
import br.com.gabryel.reginaesanguine.domain.effect.DestroyCards
import br.com.gabryel.reginaesanguine.domain.effect.Effect
import br.com.gabryel.reginaesanguine.domain.effect.EffectWithAffected
import br.com.gabryel.reginaesanguine.domain.effect.GameSummarizer
import br.com.gabryel.reginaesanguine.domain.effect.None
import br.com.gabryel.reginaesanguine.domain.effect.PlayerEffect
import br.com.gabryel.reginaesanguine.domain.effect.PlayerModification
import br.com.gabryel.reginaesanguine.domain.effect.Raisable
import br.com.gabryel.reginaesanguine.domain.effect.Scoped
import br.com.gabryel.reginaesanguine.domain.effect.TargetType
import br.com.gabryel.reginaesanguine.domain.effect.Trigger
import br.com.gabryel.reginaesanguine.domain.effect.WhenDestroyed
import br.com.gabryel.reginaesanguine.domain.effect.WhenPlayed
import br.com.gabryel.reginaesanguine.domain.effect.WhileActive
import kotlin.collections.fold
import kotlin.reflect.KClass

data class EffectSource(val player: PlayerPosition, val effect: Effect, val position: Position)

data class EffectApplicationResult(
    val effectRegistry: EffectRegistry,
    val playerModifications: Map<PlayerPosition, PlayerModification> = emptyMap()
)

data class EffectRegistryWithPlayerModifications(
    val effectRegistry: EffectRegistry,
    val playerModifications: Map<PlayerPosition, PlayerModification> = emptyMap()
)

data class EffectRegistry(
    private val triggered: Map<Position, List<EffectSource>> = emptyMap(),
    private val byTrigger: Map<KClass<out Trigger>, Map<Position, EffectSource>> = emptyMap()
) {
    private val activeEffects = gatherActiveEffects()

    fun onPlaceCard(player: PlayerPosition, effect: Effect, position: Position, board: CellContainer): EffectApplicationResult {
        val newRegistry = addTrigger(effect, position, player)
        val result = newRegistry.triggerWhenPlayedWithPlayerEffects(board, position)

        return EffectApplicationResult(result.effectRegistry, result.playerModifications)
    }

    fun onDestroy(positions: Set<Position>, board: CellContainer): EffectApplicationResult {
        val newRegistry = triggerWhenDestroyed(positions, board)
            .removeTriggered(positions)
            .removeTrigger(positions)

        // Collect PlayerEffect modifications from WhenDestroyed triggers
        val playerModifications = collectWhenDestroyedPlayerEffects(positions, board)

        return EffectApplicationResult(newRegistry, playerModifications)
    }

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

    private fun triggerWhenPlayedWithPlayerEffects(
        board: CellContainer,
        position: Position
    ): EffectRegistryWithPlayerModifications {
        val playedCell = board.getCellAt(position).orNull() ?: return EffectRegistryWithPlayerModifications(this)
        val playedPlayer = playedCell.owner ?: return EffectRegistryWithPlayerModifications(this)

        val whenPlayedTriggers = byTrigger[WhenPlayed::class].orEmpty()
        val summarizer = GameSummarizer.forBoard(board, this)

        val (newRegistry, playerModifications) = whenPlayedTriggers.values.fold(
            this to emptyMap<PlayerPosition, PlayerModification>(),
        ) { (accRegistry, accModifications), sourceEffect ->
            val trigger = sourceEffect.effect.trigger as? WhenPlayed ?: return@fold accRegistry to accModifications

            // Check if the played card is in scope of this trigger
            val self = position == sourceEffect.position
            if (!trigger.isInScope(sourceEffect.player, playedPlayer, self))
                return@fold accRegistry to accModifications

            // Apply board effects
            val newRegistry = accRegistry.applyEffect(sourceEffect, board)

            // Collect player effects
            val newPlayerModifications = if (sourceEffect.effect is PlayerEffect) {
                val effectModifications = sourceEffect.effect.getPlayerModifications(summarizer, sourceEffect.player, sourceEffect.position)
                mergePlayerModificationsInternal(accModifications, effectModifications)
            } else {
                accModifications
            }

            newRegistry to newPlayerModifications
        }

        return EffectRegistryWithPlayerModifications(newRegistry, playerModifications)
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

    private fun mergePlayerModificationsInternal(
        first: Map<PlayerPosition, PlayerModification>,
        second: Map<PlayerPosition, PlayerModification>
    ): Map<PlayerPosition, PlayerModification> {
        val allPlayers = first.keys + second.keys
        return allPlayers.associateWith { player ->
            val firstMod = first[player] ?: PlayerModification()
            val secondMod = second[player] ?: PlayerModification()
            PlayerModification(cardsToAdd = firstMod.cardsToAdd + secondMod.cardsToAdd)
        }.filterValues { it.cardsToAdd.isNotEmpty() }
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

    private fun collectWhenDestroyedPlayerEffects(
        positions: Set<Position>,
        board: CellContainer
    ): Map<PlayerPosition, PlayerModification> {
        val cells = positions.mapNotNull { position ->
            board.getCellAt(position).map { position to it }.orNull()
        }

        val whenDestroyedTriggers = byTrigger[WhenDestroyed::class].orEmpty()
        val summarizer = GameSummarizer.forBoard(board, this)

        return whenDestroyedTriggers.values.fold(emptyMap()) { accModifications, sourceEffect ->
            if (sourceEffect.effect !is PlayerEffect) return@fold accModifications

            val trigger = sourceEffect.effect.trigger as? WhenDestroyed ?: return@fold accModifications

            cells.fold(accModifications) { accModifications, (targetPosition, targetCell) ->
                targetCell.card ?: return@fold accModifications
                val targetPlayer = targetCell.owner ?: return@fold accModifications
                val self = targetPosition == sourceEffect.position

                if (!trigger.isInScope(sourceEffect.player, targetPlayer, self))
                    return@fold accModifications

                val effectModifications = sourceEffect.effect.getPlayerModifications(summarizer, sourceEffect.player, sourceEffect.position)

                // Merge modifications for each player
                effectModifications.entries.fold(accModifications) { acc, (player, modification) ->
                    val existingModification = acc[player] ?: PlayerModification()
                    val mergedModification = PlayerModification(
                        cardsToAdd = existingModification.cardsToAdd + modification.cardsToAdd,
                    )
                    acc + (player to mergedModification)
                }
            }
        }
    }
}
