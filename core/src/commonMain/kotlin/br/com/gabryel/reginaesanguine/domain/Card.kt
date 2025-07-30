package br.com.gabryel.reginaesanguine.domain

data class Card(
    val id: String,
    val name: String,
    val increments: Set<Position>,
    val power: Int,
    val rank: Int,
    val effectDisplacements: List<Position> = listOf(),
    val effects: List<Effect> = listOf(),
) {
    override fun toString() = "[$id] [$name]"
}
