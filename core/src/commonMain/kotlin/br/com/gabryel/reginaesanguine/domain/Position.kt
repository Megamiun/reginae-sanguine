package br.com.gabryel.reginaesanguine.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Position(val x: Int, val y: Int) {
    @Transient
    val column = x

    @Transient
    val lane = y

    operator fun plus(displacement: Displacement) =
        (lane + displacement.lane) atColumn (column + displacement.column)

    fun constrainTo(size: Size) =
        lane.mod(size.height) atColumn column.mod(size.width)

    fun asDisplacement() = Displacement(x, y)
}

infix fun Int.atLane(lane: Int) = Position(this, lane)

infix fun Int.atColumn(column: Int) = Position(column, this)

@Serializable
data class Displacement(val x: Int, val y: Int) {
    companion object {
        val UPWARD = Displacement(0, -1)
        val DOWNWARD = Displacement(0, 1)
        val LEFTWARD = Displacement(-1, 0)
        val RIGHTWARD = Displacement(1, 0)
    }

    @Transient
    val column = x

    @Transient
    val lane = y

    fun mirrorHorizontal() = copy(x = x * -1)

    operator fun plus(displacement: Displacement) =
        Displacement(x + displacement.y, y + displacement.y)
}

@Serializable
class Size(val width: Int, val height: Int) {
    operator fun contains(position: Position) =
        position.lane in 0 until height && position.column in 0 until width
}
