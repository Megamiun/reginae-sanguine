package br.com.gabryel.reginaesanguine.domain

interface CellContainer {
    fun getCellAt(position: Position): Result<Cell>
}
