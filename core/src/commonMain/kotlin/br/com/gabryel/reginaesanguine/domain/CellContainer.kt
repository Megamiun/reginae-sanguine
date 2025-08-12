package br.com.gabryel.reginaesanguine.domain

interface CellContainer {
    val size: Size

    fun getCellAt(position: Position): Result<Cell>

    fun getTotalScoreAt(position: Position): Result<Int>

    fun getBaseLaneScoreAt(lane: Int): Map<PlayerPosition, Int>

    fun getExtraLaneScoreAt(lane: Int): Map<PlayerPosition, Int>

    fun getOccupiedCells(): Map<Position, Cell>
}
