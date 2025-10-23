package br.com.gabryel.reginaesanguine.server.domain

import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.GameView
import br.com.gabryel.reginaesanguine.domain.PlayableMove
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Data transfer object for GameView.
 * Represents the minimal view for remote clients with only essential information.
 */
@Serializable
data class GameViewDto(
    val packId: String,
    val localPlayerHand: List<String>,
    val localPlayerDeckSize: Int,
    val localPlayerPosition: PlayerPosition,
    val playerTurn: PlayerPosition,
    val round: Int,
    val state: StateDto,
    val boardCells: Map<Position, @Contextual Cell>,
    val boardScores: Map<PlayerPosition, Int>,
    val laneScores: Map<Int, Map<PlayerPosition, Int>>,
    val laneWinners: Map<Int, PlayerPosition?>,
    val playableMoves: Set<PlayableMove>
) {
    companion object {
        fun from(gameView: GameView, packId: String): GameViewDto =
            GameViewDto(
                packId = packId,
                localPlayerHand = gameView.localPlayerHand.map { it.id },
                localPlayerDeckSize = gameView.localPlayerDeckSize,
                localPlayerPosition = gameView.localPlayerPosition,
                playerTurn = gameView.playerTurn,
                round = gameView.round,
                state = StateDto.from(gameView.state),
                boardCells = gameView.boardCells,
                boardScores = gameView.boardScores,
                laneScores = gameView.laneScores,
                laneWinners = gameView.laneWinners,
                playableMoves = gameView.playableMoves,
            )
    }
}
