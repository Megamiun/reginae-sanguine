package br.com.gabryel.reginaesanguine.viewmodel.game.remote

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.DEFAULT_BOARD_SIZE
import br.com.gabryel.reginaesanguine.domain.PlayableMove
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.State
import br.com.gabryel.reginaesanguine.server.domain.GameViewDto
import br.com.gabryel.reginaesanguine.server.domain.StateDto.Ended.Tie
import br.com.gabryel.reginaesanguine.server.domain.StateDto.Ended.Won
import br.com.gabryel.reginaesanguine.server.domain.StateDto.Ongoing
import br.com.gabryel.reginaesanguine.viewmodel.game.GamePlayerSummary

/**
 * GameUIData implementation for remote games using GameViewDto.
 * Only provides information that is safe for remote clients.
 * Caches playable moves from the server to avoid recalculation.
 *
 * Note: currentPlayerHand returns empty list as card details should be
 * fetched separately using the pack ID and card IDs.
 */
class RemoteGameStateData(
    private val gameView: GameViewDto,
    private val availableCards: Map<String, Card>
) : GamePlayerSummary {
    override val size = DEFAULT_BOARD_SIZE
    override val localPlayerPosition = gameView.localPlayerPosition
    override val currentPlayerHand = gameView.localPlayerHand.mapNotNull { availableCards[it] }
    override val round = gameView.round
    override val id = gameView.id

    val playableMoves: Set<PlayableMove> = gameView.playableMoves

    override fun getScores() = gameView.boardScores
        .associate { it.player to it.score }

    override fun getCellAt(position: Position) = gameView.boardCells
        .firstOrNull { it.position == position }
        ?.cell

    override fun getBaseLaneScoreAt(lane: Int) = gameView.laneScores
        .firstOrNull { it.lane == lane }
        ?.scores
        ?.associate { it.player to it.score }
        ?: emptyMap()

    override fun getLaneWinner(lane: Int) = gameView.laneWinners
        .firstOrNull { it.lane == lane }
        ?.winner

    override fun getState(): State = when (val state = gameView.state) {
        is Ongoing -> State.Ongoing
        is Tie -> State.Ended.Tie
        is Won -> State.Ended.Won(state.player)
    }

    override fun getWinner(): PlayerPosition? = when (val state = gameView.state) {
        is Won -> state.player
        else -> null
    }
}
