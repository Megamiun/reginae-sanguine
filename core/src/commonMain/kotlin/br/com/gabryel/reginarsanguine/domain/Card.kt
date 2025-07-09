package br.com.gabryel.reginarsanguine.domain

data class Card(val name: String, val increments: Map<Position, Int>, val value: Int, val price: Int) {
    val id: String = name

    override fun toString() = "($price) [$name]"
}
