package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.Board
import br.com.gabryel.reginaesanguine.domain.EffectRegistry
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position

data class EffectContext(
    val sourcePosition: Position,
    val sourcePlayer: PlayerPosition,
    val board: Board,
    val registry: EffectRegistry
)
