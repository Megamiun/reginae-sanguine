package br.com.gabryel.reginaesanguine.domain

import br.com.gabryel.reginaesanguine.domain.effect.DestroyCards
import br.com.gabryel.reginaesanguine.domain.effect.Effect
import br.com.gabryel.reginaesanguine.domain.effect.EffectWithAffected
import br.com.gabryel.reginaesanguine.domain.effect.RaisePower
import br.com.gabryel.reginaesanguine.domain.effect.RaiseRank
import br.com.gabryel.reginaesanguine.domain.effect.Targetable
import br.com.gabryel.reginaesanguine.domain.effect.Trigger
import br.com.gabryel.reginaesanguine.domain.effect.WhenDestroyed
import br.com.gabryel.reginaesanguine.domain.effect.WhenPlayed
import kotlin.collections.emptyMap
import kotlin.reflect.KClass

typealias PlayerEffect = Pair<PlayerPosition, Effect>

data class EffectRegistry(
    private val appliedEffect: Map<Position, List<PlayerEffect>> = emptyMap(),
    private val byTrigger: Map<KClass<out Trigger>, Map<Position, PlayerEffect>> = emptyMap()
) {
    fun onPlaceCard(player: PlayerPosition, effect: Effect?, position: Position): EffectRegistry {
        if (effect == null)
            return this

        if (effect.trigger is WhenPlayed) {
            return runPermanentEffect(effect, position, player)
        }

        val triggerType = effect.trigger::class
        val orElse = byTrigger.getOrElse(triggerType) { emptyMap() } + (position to (player to effect))
        val newByTrigger = byTrigger + (triggerType to orElse)

        return this.copy(byTrigger = newByTrigger)
    }

    fun onDestroy(positions: List<Position>): EffectRegistry {
        val onDestroy = byTrigger[WhenDestroyed::class].orEmpty()

        return positions.fold(this) { acc, position ->
            val (player, effect) = onDestroy[position] ?: return@fold acc

            acc.runPermanentEffect(effect, position, player)
        }
    }

    fun getExtraPowerAt(target: PlayerPosition, position: Position): Int = appliedEffect[position].orEmpty()
        .filter { (source, effect) -> (effect as? RaisePower)?.target?.isTargetable(source, target) ?: false }
        .sumOf { (_, effect) -> (effect as RaisePower).amount }

    fun getDestroyable(): Set<Pair<PlayerPosition, Position>> = PlayerPosition.entries.flatMap { target ->
        appliedEffect
            .filterValues { it.any { (source, effect) -> isApplicableTo<DestroyCards>(effect, source, target) } }
            .flatMap { (position, playerEffects) -> playerEffects.map { (playerPosition, _) -> playerPosition to position } }
    }.toSet()

    private fun runPermanentEffect(effect: Effect, position: Position, player: PlayerPosition): EffectRegistry {
        if (effect is EffectWithAffected) {
            val affected = effect.affected
                .map { displacement -> position + player.correct(displacement) }

            val ownerEffect = player to effect
            val newAppliedEffect = affected.fold(appliedEffect) { acc, position ->
                acc + (position to (acc[position].orEmpty() + ownerEffect))
            }

            return copy(appliedEffect = newAppliedEffect)
        }
        // TODO move to loggers
        println("Not implemented yet: $effect")
        return this
    }

    private inline fun <reified T : Effect> isApplicableTo(effect: Effect, source: PlayerPosition, target: PlayerPosition): Boolean {
        if (effect !is T) return false

        return effect !is Targetable || effect.target.isTargetable(source, target)
    }
}
