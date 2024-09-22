package br.com.gabryel.reginarsanguine.domain

interface CellContainer {
    fun getCellAt(position: Position): Result<Cell>
}
