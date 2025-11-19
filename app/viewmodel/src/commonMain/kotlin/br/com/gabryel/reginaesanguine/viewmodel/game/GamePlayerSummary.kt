package br.com.gabryel.reginaesanguine.viewmodel.game

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.Size
import br.com.gabryel.reginaesanguine.domain.State

/**
 * Interface providing UI-safe access to game information.
 * This ensures the UI gets the information it needs without breaking the GameClient abstraction.
 */
interface GamePlayerSummary {
    val size: Size
    val playerTurn: PlayerPosition
    val localPlayerPosition: PlayerPosition
    val currentPlayerHand: List<Card>
    val round: Int

    fun getState(): State

    fun getScores(): Map<PlayerPosition, Int>

    fun getWinner(): PlayerPosition?

    fun getCellAt(position: Position): Cell?

    fun getBaseLaneScoreAt(lane: Int): Map<PlayerPosition, Int>

    fun getLaneWinner(lane: Int): PlayerPosition?
}
