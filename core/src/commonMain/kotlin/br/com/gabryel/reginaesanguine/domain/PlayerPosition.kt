package br.com.gabryel.reginaesanguine.domain

enum class PlayerPosition(getNext: Lazy<PlayerPosition>) {
    LEFT(lazy { RIGHT }),
    RIGHT(lazy { LEFT });

    val opponent by getNext

    fun correct(displacement: Displacement) = when (this) {
        LEFT -> displacement
        RIGHT -> displacement.mirrorHorizontal()
    }
}
