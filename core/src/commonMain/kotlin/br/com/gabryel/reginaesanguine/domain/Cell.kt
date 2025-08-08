package br.com.gabryel.reginaesanguine.domain

import kotlin.math.min

data class Cell(
    val owner: PlayerPosition? = null,
    val rank: Int = 0,
    val card: Card? = null
) {
    companion object {
        val EMPTY = Cell()
    }

    fun increment(player: PlayerPosition, inc: Int): Cell = when {
        card != null -> this
        // TODO Validate what happens if player steals cell from another player
        player.opponent == owner -> copy(owner = player)
        else -> copy(owner = player, rank = min(3, rank + inc))
    }
}
