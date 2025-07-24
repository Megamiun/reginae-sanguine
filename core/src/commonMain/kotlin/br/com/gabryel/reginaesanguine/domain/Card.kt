package br.com.gabryel.reginaesanguine.domain

data class Card(
    val name: String,
    val increments: Map<Position, Int>,
    val power: Int,
    val rank: Int,
    val effectDisplacements: List<Position> = listOf(),
    val effects: List<Effect> = listOf(),
) {
    val id: String = name

    override fun toString() = "($rank) [$name]"
}
