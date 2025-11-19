package br.com.gabryel.reginaesanguine.viewmodel.game.local

import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.viewmodel.game.GamePlayerSummary

/**
 * GameUIData implementation for local games with full Game access.
 * Provides all necessary UI information from the complete game state.
 */
class LocalGameStateData(private val game: Game, override val localPlayerPosition: PlayerPosition = LEFT) :
    GamePlayerSummary {
    override val size = game.size
    override val playerTurn = game.playerTurn
    override val currentPlayerHand = game.players[localPlayerPosition]?.hand ?: emptyList()
    override val round = game.round

    override fun getState() = game.getState()

    override fun getScores() = game.getScores()

    override fun getWinner() = game.getWinner()

    override fun getCellAt(position: Position): Cell? = game.getCellAt(position).orNull()

    override fun getBaseLaneScoreAt(lane: Int): Map<PlayerPosition, Int> = game.getBaseLaneScoreAt(lane)

    override fun getLaneWinner(lane: Int): PlayerPosition? = game.getLaneWinner(lane)
}
