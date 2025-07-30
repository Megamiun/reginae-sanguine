package br.com.gabryel.reginaesanguine.domain

import br.com.gabryel.reginaesanguine.domain.effect.Effect

data class Card(
    val id: String,
    val name: String,
    val increments: Set<Position>,
    val power: Int,
    val rank: Int,
    val affected: List<Position> = listOf(),
    val effect: Effect? = null
) {
    override fun toString() = "[$id] [$name]"
}
