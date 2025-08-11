package br.com.gabryel.reginaesanguine.domain

interface CellContainer {
    val size: Size

    fun getCellAt(position: Position): Result<Cell>

    fun getScoreAt(position: Position): Result<Int>

    fun getOccupiedCells(): Map<Position, Cell>
}
