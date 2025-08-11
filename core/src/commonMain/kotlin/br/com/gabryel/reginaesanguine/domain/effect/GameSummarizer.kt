package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.CellContainer
import br.com.gabryel.reginaesanguine.domain.EffectRegistry
import br.com.gabryel.reginaesanguine.domain.Position

interface GameSummarizer : CellContainer {
    companion object {
        fun forBoard(board: CellContainer, effectRegistry: EffectRegistry) =
            DefaultGameSummarizer(board, effectRegistry)
    }

    fun getExtraPowerAt(sourcePosition: Position): Int
}

class DefaultGameSummarizer(
    val board: CellContainer,
    val effectRegistry: EffectRegistry
) : GameSummarizer, CellContainer by board {
    override fun getExtraPowerAt(sourcePosition: Position) = effectRegistry.getExtraPowerAt(sourcePosition, board)
}
