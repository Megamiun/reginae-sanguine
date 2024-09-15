package br.com.gabryel.reginarsanguine.domain

data class Cell(val owner: PlayerPosition? = null, val pins: Int = 0, val card: Card? = null) {
    companion object {
        val EMPTY = Cell()
    }
}