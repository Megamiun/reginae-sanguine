package br.com.gabryel.reginarsanguine.domain

data class Card(val name: String, val increments: Map<Position, Int>, val power: Int, val cost: Int, val effects: List<Effect> = listOf()) {
    val id: String = name

    override fun toString() = "($cost) [$name]"
}
