package br.com.gabryel.reginaesanguine.server.domain

import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.Position
import kotlinx.serialization.Serializable

/**
 * Represents a single cell on the board with its position.
 */
@Serializable
data class BoardCellDto(
    val position: Position,
    val cell: Cell
) {
    companion object {
        fun from(position: Position, cell: Cell) = BoardCellDto(position, cell)
    }
}
