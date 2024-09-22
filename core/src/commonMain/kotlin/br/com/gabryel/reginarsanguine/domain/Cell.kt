package br.com.gabryel.reginarsanguine.domain

import kotlin.math.min

data class Cell(val owner: PlayerPosition? = null, val pins: Int = 0, val card: Card? = null) {
    companion object {
        val EMPTY = Cell()
    }

    fun increment(
        player: PlayerPosition,
        inc: Int,
    ): Cell =
        if (card != null) {
            this
        } else if (owner == null || player == owner) {
            copy(owner = player, pins = min(3, pins + inc))
        } else {
            copy(owner = player)
        }
}
