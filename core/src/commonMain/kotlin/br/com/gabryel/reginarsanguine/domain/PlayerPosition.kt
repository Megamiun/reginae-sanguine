package br.com.gabryel.reginarsanguine.domain

enum class PlayerPosition(getNext: Lazy<PlayerPosition>) {
    LEFT(lazy { RIGHT }),
    RIGHT(lazy { LEFT });

    val next by getNext

    fun correct(displacement: Displacement) = when (this) {
        LEFT -> displacement
        RIGHT -> displacement.lane() to displacement.column() * -1
    }
}
