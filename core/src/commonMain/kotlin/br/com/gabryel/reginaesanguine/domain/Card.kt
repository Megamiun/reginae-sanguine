package br.com.gabryel.reginaesanguine.domain

import br.com.gabryel.reginaesanguine.domain.effect.Effect
import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val id: String,
    val name: String,
    val increments: Set<Displacement> = setOf(),
    val power: Int,
    val rank: Int,
    val affected: Set<Displacement> = setOf(),
    val effect: Effect? = null
) {
    override fun toString() = "[$id] [$name]"
}
