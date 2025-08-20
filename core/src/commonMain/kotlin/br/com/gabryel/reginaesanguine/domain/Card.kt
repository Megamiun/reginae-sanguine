package br.com.gabryel.reginaesanguine.domain

import br.com.gabryel.reginaesanguine.domain.effect.type.Effect
import br.com.gabryel.reginaesanguine.domain.effect.type.NoEffect
import br.com.gabryel.reginaesanguine.domain.effect.type.RaiseRankDefault
import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val id: String,
    val name: String,
    val tier: CardTier,
    val power: Int,
    val rank: Int = 0,
    val spawnOnly: Boolean = false,
    val increments: Set<Displacement> = setOf(),
    val effect: Effect = NoEffect
) {
    val incrementValue = (effect as? RaiseRankDefault)?.amount ?: 1

    override fun toString() = "[$id] [$name]"
}
