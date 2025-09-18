package br.com.gabryel.reginaesanguine.domain.effect

import arrow.core.fold
import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.CellContainer
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.SELF
import br.com.gabryel.reginaesanguine.domain.effect.type.AddCardsToHand
import br.com.gabryel.reginaesanguine.domain.effect.type.DestroyCards
import br.com.gabryel.reginaesanguine.domain.effect.type.Effect
import br.com.gabryel.reginaesanguine.domain.effect.type.EffectWithAffected
import br.com.gabryel.reginaesanguine.domain.effect.type.RaiseCell
import br.com.gabryel.reginaesanguine.domain.effect.type.RaiseLane
import br.com.gabryel.reginaesanguine.domain.effect.type.Spawn
import kotlin.reflect.KClass

val DEFAULT_CONDITIONAL_TYPES = listOf<KClass<out Conditional>>(WhenFirstReachesPower::class, WhenFirstStatusChanged::class)

data class EffectSource<T : Effect>(val player: PlayerPosition, val effect: T, val position: Position) {
    inline fun <reified V : T> upcastOrNull(): EffectSource<V>? =
        if (effect is V) EffectSource(player, effect, position) else null
}

data class EffectApplicationResult(
    val effectRegistry: EffectRegistry,
    val toAddToHand: Map<PlayerPosition, List<String>> = emptyMap(),
    val toAddToBoard: Map<PlayerPosition, Map<Position, String>> = emptyMap(),
    val toDelete: Set<Position> = emptySet()
)

data class EffectRegistry(
    private val appliedRaises: Map<Position, List<EffectSource<RaiseCell>>> = emptyMap(),
    private val byTrigger: Map<KClass<out Trigger>, Map<Position, EffectSource<Effect>>> = emptyMap(),
    private val conditionalTypes: List<KClass<out Conditional>> = DEFAULT_CONDITIONAL_TYPES
) {
    private val activeEffects = gatherActiveEffects()

    fun onPlaceCard(player: PlayerPosition, effect: Effect, position: Position, board: CellContainer): EffectApplicationResult =
        addTrigger(effect, position, player)
            .triggerScoped<WhenPlayed>(listOf(position to board.getCellAt(position).orNull()!!), board)

    fun onSpawnCard(player: PlayerPosition, effect: Effect, position: Position, board: CellContainer): EffectApplicationResult =
        addTrigger(effect, position, player)
            .triggerScoped<WhenPlayed>(listOf(position to board.getCellAt(position).orNull()!!), board, true)

    fun onDestroy(positions: Set<Position>, board: CellContainer): EffectApplicationResult =
        triggerWhenDestroyed(positions, board).let { result ->
            val cleanRegistry = result.effectRegistry
                .removeTriggered(positions)
                .removeTrigger(positions)

            result.copy(effectRegistry = cleanRegistry)
        }

    fun checkConditionals(board: CellContainer): EffectApplicationResult {
        val conditionalTypes = byTrigger.filter { (type) -> conditionalTypes.any { it == type } }.values
        return conditionalTypes.fold(EffectApplicationResult(this)) { acc, conditionalTriggers ->
            conditionalTriggers.fold(acc) { accResult, (position, sourceEffect) ->
                val trigger = sourceEffect.effect.trigger as? Conditional ?: return accResult

                val summarizer = GameSummarizer.forBoard(board, accResult.effectRegistry)
                if (!trigger.isSatisfied(summarizer, position)) return@fold accResult

                val result = sourceEffect.trigger(listOf(position), board, accResult)
                result.copy(effectRegistry = result.effectRegistry.removeTrigger(position, trigger::class))
            }
        }
    }

    fun getExtraPowerAtLane(lane: Int, board: CellContainer): Map<PlayerPosition, Int> {
        val summarizer = GameSummarizer.forBoard(board, this)

        return byTrigger[WhenLaneWon::class].orEmpty().map { (position, sourceEffect) ->
            when (val effect = sourceEffect.effect) {
                is RaiseLane ->
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

    fun getDestroyable(board: CellContainer): Set<Position> = activeEffects.keys.filter { position ->
        val cell = board.getCellAt(position).orNull() ?: return@filter false
        val card = cell.card ?: return@filter false

        card.power - getExtraPowerAt(position, board) > 0
    }.toSet()

    private fun removeTriggered(positions: Set<Position>): EffectRegistry =
        copy(appliedRaises = appliedRaises - positions)

    private fun removeTrigger(positions: Set<Position>): EffectRegistry =
        copy(byTrigger = byTrigger.mapValues { (_, byTriggerType) -> byTriggerType - positions })

    private fun removeTrigger(position: Position, type: KClass<*>): EffectRegistry =
        copy(
            byTrigger = byTrigger.mapValues { (mapType, byTriggerType) ->
                if (type != mapType) return@mapValues byTriggerType

                byTriggerType.filterKeys { mapPosition -> position != mapPosition }
            },
        )

    private fun triggerWhenDestroyed(positions: Set<Position>, board: CellContainer): EffectApplicationResult {
        val cells = positions.mapNotNull { position ->
            board.getCellAt(position).map { position to it }.orNull()
        }

        return triggerScoped<WhenDestroyed>(cells, board)
    }

    private inline fun <reified T> triggerScoped(
        cells: List<Pair<Position, Cell>>,
        board: CellContainer,
        spawned: Boolean = false
    ): EffectApplicationResult where T : Trigger, T : Scoped {
        val scopedTriggers = byTrigger[T::class].orEmpty()

        return scopedTriggers.fold(EffectApplicationResult(this)) { accResult, (_, sourceEffect) ->
            val trigger = sourceEffect.effect.trigger as? T ?: return accResult

            if (spawned && trigger.scope != SELF) return accResult

            val cells = cells.filter { cell -> sourceEffect.isInScope(cell, trigger) }
            if (cells.isEmpty()) return@fold accResult

            sourceEffect.trigger(cells.map { it.first }, board, accResult)
        }
    }

    private fun EffectSource<Effect>.trigger(
        cells: List<Position>,
        board: CellContainer,
        result: EffectApplicationResult
    ): EffectApplicationResult {
        val summarizer = GameSummarizer.forBoard(board, result.effectRegistry)

        return when (effect) {
            is RaiseCell -> {
                val newRegistry = cells.fold(result.effectRegistry) { accRegistry, _ ->
                    accRegistry.applyRaise(upcastOrNull<RaiseCell>()!!, board)
                }
                result.copy(effectRegistry = newRegistry)
            }
            is AddCardsToHand -> {
                val modifications = cells.fold(result.toAddToHand) { accModifications, _ ->
                    accModifications + effect.getNewCards(summarizer, player, position)
                }
                result.copy(toAddToHand = modifications)
            }
            is DestroyCards -> {
                val toDelete = effect.getAffectedPositions(position, player)
                result.copy(toDelete = result.toDelete + toDelete)
            }
            is Spawn -> {
                val toSpawn = effect.getSpawns(summarizer, player)
                result.copy(toAddToBoard = toSpawn)
            }
            else -> result
        }
    }

    private fun EffectSource<*>.isInScope(cell: Pair<Position, Cell>, trigger: Scoped): Boolean {
        cell.second.card ?: return false
        val targetPlayer = cell.second.owner ?: return false

        return trigger.isInScope(player, targetPlayer, cell.first == position)
    }

    private fun applyRaise(sourceEffect: EffectSource<RaiseCell>, board: CellContainer): EffectRegistry {
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

    private fun gatherActiveEffects(): Map<Position, List<EffectSource<RaiseCell>>> {
        val triggered = appliedRaises.flatMap { (_, effectSources) ->
            effectSources.flatMap { effectSource -> effectSource.getAffected().map { it to effectSource } }
        }

        val whileActive = byTrigger[WhileActive::class].orEmpty()
            .mapNotNull { (l, r) -> r.upcastOrNull<RaiseCell>()?.let { l to it } }
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
