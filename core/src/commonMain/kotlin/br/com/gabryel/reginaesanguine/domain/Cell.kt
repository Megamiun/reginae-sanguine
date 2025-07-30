package br.com.gabryel.reginaesanguine.domain

import br.com.gabryel.reginaesanguine.domain.effect.Effect
import br.com.gabryel.reginaesanguine.domain.effect.RaisePower
import kotlin.math.min

data class Cell(
    val owner: PlayerPosition? = null,
    val rank: Int = 0,
    val card: Card? = null,
    val appliedEffects: List<Pair<PlayerPosition, Effect>> = listOf()
) {
    companion object {
        val EMPTY = Cell()
    }

    fun increment(player: PlayerPosition, inc: Int): Cell = when {
        card != null -> this
        owner == null || player == owner ->
            copy(owner = player, rank = min(3, rank + inc))
        else -> copy(owner = player)
    }

    val totalPower = card?.let {
        owner?.let {
            val addedPower = appliedEffects
                // TODO Only considers ally Raise for now
                .filter { effect -> effect.first == owner }
                .map { it.second }
                .filterIsInstance<RaisePower>()
                .sumOf { it.amount }

            card.power + addedPower
        }
    }
}
