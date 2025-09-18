package br.com.gabryel.reginaesanguine.viewmodel.game

import br.com.gabryel.reginaesanguine.domain.Action
import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.Failure
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.Success
import br.com.gabryel.reginaesanguine.viewmodel.game.GameState.ChooseAction

/**
 * GameUIData implementation for local games with full Game access.
 * Provides all necessary UI information from the complete game state.
 */
class LocalGameStateData(private val game: Game, override val localPlayerPosition: PlayerPosition = LEFT) : GameStateData {
    override val size = game.size
    override val playerTurn = game.playerTurn
    override val playerHandPosition = game.playerTurn
    override val currentPlayerHand = game.players[playerHandPosition]?.hand ?: emptyList()
    override val round = game.round

    override fun getState() = game.getState()

    override fun getScores() = game.getScores()

    override fun getWinner() = game.getWinner()

    override fun getCellAt(position: Position): Cell? =
        when (val result = game.getCellAt(position)) {
            is Success<Cell> -> result.value
            is Failure -> null
        }

    override fun getBaseLaneScoreAt(lane: Int): Map<PlayerPosition, Int> = game.getBaseLaneScoreAt(lane)

    override fun getLaneWinner(lane: Int): PlayerPosition? = game.getLaneWinner(lane)
}

/**
 * Local game client that executes game actions immediately using the full game state.
 * This maintains the original local gameplay behavior where both players' information
 * is available and actions are executed synchronously.
 */
class LocalGameManager(private val currentGame: Game) : GameManager {
    override suspend fun skip() = update(Action.Skip)

    override suspend fun play(position: Position, cardId: String) = update(Play(position, cardId))

    private fun update(action: Action<out String>): GameState =
        when (val result = currentGame.play(currentGame.playerTurn, action)) {
            is Success<Game> -> ChooseAction(LocalGameManager(result.value), LocalGameStateData(result.value))
            is Failure -> ChooseAction(this, LocalGameStateData(currentGame), error = result.toString())
        }

    override fun isPlayable(position: Position, cardId: String): Boolean =
        currentGame.play(currentGame.playerTurn, Play(position, cardId)) is Success
}
