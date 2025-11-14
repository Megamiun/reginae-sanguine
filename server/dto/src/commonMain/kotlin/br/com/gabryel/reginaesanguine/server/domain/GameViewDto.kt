package br.com.gabryel.reginaesanguine.server.domain

import br.com.gabryel.reginaesanguine.domain.GameView
import br.com.gabryel.reginaesanguine.domain.PlayableMove
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
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
    val boardCells: List<BoardCellDto>,
    val boardScores: List<PlayerScoreDto>,
    val laneScores: List<LaneScoreDto>,
    val laneWinners: List<LaneWinnerDto>,
    val playableMoves: Set<PlayableMove>
) {
    companion object {
        fun from(gameView: GameView, packId: String) = GameViewDto(
            packId = packId,
            localPlayerHand = gameView.localPlayerHand.map { it.id },
            localPlayerDeckSize = gameView.localPlayerDeckSize,
            localPlayerPosition = gameView.localPlayerPosition,
            playerTurn = gameView.playerTurn,
            round = gameView.round,
            state = StateDto.from(gameView.state),
            boardCells = gameView.boardCells.map { (position, cell) ->
                BoardCellDto.from(position, cell)
            },
            boardScores = gameView.boardScores.map { (player, score) ->
                PlayerScoreDto.from(player, score)
            },
            laneScores = gameView.laneScores.map { (lane, scores) ->
                LaneScoreDto.from(lane, scores)
            },
            laneWinners = gameView.laneWinners.map { (lane, winner) ->
                LaneWinnerDto.from(lane, winner)
            },
            playableMoves = gameView.playableMoves,
        )
    }
}
