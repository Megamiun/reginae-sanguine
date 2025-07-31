package br.com.gabryel.reginaesanguine.domain

interface CellContainer {
    val size: Size

    fun getCellAt(position: Position): Result<Cell>
}
