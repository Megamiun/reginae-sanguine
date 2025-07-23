package br.com.gabryel.reginaesanguine.domain

interface CellContainer {
    val width: Int
    val height: Int

    fun getCellAt(position: Position): Result<Cell>
}
