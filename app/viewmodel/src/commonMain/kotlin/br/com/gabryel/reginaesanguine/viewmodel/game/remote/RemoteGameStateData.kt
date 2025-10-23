package br.com.gabryel.reginaesanguine.viewmodel.game.remote

import br.com.gabryel.reginaesanguine.domain.DEFAULT_BOARD_SIZE
import br.com.gabryel.reginaesanguine.domain.GameView
import br.com.gabryel.reginaesanguine.domain.PlayableMove
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.viewmodel.game.GamePlayerSummary

/**
 * GameUIData implementation for remote games using GameView.
 * Only provides information that is safe for remote clients.
 * Caches playable moves from the server to avoid recalculation.
 */
class RemoteGameStateData(private val gameView: GameView) : GamePlayerSummary {
    override val size = DEFAULT_BOARD_SIZE
    override val playerTurn = gameView.playerTurn
    override val playerHandPosition = gameView.localPlayerPosition
    override val localPlayerPosition = gameView.localPlayerPosition
    override val currentPlayerHand = gameView.localPlayerHand
    override val round = gameView.round

    val playableMoves: Set<PlayableMove> = gameView.playableMoves

    override fun getState() = gameView.state

    override fun getScores() = gameView.boardScores

    override fun getWinner() = gameView.getWinner()

    override fun getCellAt(position: Position) = gameView.boardCells[position]

    override fun getBaseLaneScoreAt(lane: Int) = gameView.laneScores[lane] ?: emptyMap()

    override fun getLaneWinner(lane: Int) = gameView.laneWinners[lane]
}
