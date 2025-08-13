package br.com.gabryel.reginaesanguine.domain

import br.com.gabryel.reginaesanguine.domain.effect.type.Effect
import br.com.gabryel.reginaesanguine.domain.effect.type.NoEffect
import br.com.gabryel.reginaesanguine.domain.effect.type.RaiseRank
import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val id: String,
    val name: String,
    val increments: Set<Displacement> = setOf(),
    val power: Int,
    val rank: Int,
    val effect: Effect = NoEffect
) {
    val incrementValue = (effect as? RaiseRank)?.amount ?: 1

    override fun toString() = "[$id] [$name]"
}
