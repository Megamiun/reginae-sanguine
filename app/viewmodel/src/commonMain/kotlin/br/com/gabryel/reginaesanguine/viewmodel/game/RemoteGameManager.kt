package br.com.gabryel.reginaesanguine.viewmodel.game

import br.com.gabryel.reginaesanguine.domain.Action
import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.DEFAULT_BOARD_SIZE
import br.com.gabryel.reginaesanguine.domain.Failure
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.GameView
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.State
import br.com.gabryel.reginaesanguine.domain.Success
import br.com.gabryel.reginaesanguine.viewmodel.game.GameState.ChooseAction
import br.com.gabryel.reginaesanguine.viewmodel.game.GameState.Wait
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * GameUIData implementation for remote games using GameView.
 * Only provides information that is safe for remote clients.
 */
class RemoteGameUIData(private val gameView: GameView) : GameStateData {
    override val size = DEFAULT_BOARD_SIZE
    override val playerTurn = gameView.playerTurn
    override val playerHandPosition = gameView.localPlayerPosition
    override val localPlayerPosition = gameView.localPlayerPosition
    override val currentPlayerHand = gameView.localPlayerHand
    override val round = gameView.round

    override fun getState() = gameView.state

    override fun getScores() = gameView.getScores()

    override fun getWinner() = gameView.getWinner()

    override fun getCellAt(position: Position) = gameView.boardCells[position]

    override fun getBaseLaneScoreAt(lane: Int) = gameView.laneScores[lane] ?: emptyMap()

    override fun getLaneWinner(lane: Int) = gameView.laneWinners[lane]
}

/**
 * Remote game client that only exposes minimal GameView information.
 * Never exposes full Game objects to maintain security.
 *
 * - Local player: Actions are executed immediately via server
 * - Remote player: Waits 2 seconds then automatically skips
 */
class RemoteGameManager(private val gameView: GameView, private val gameClient: GameClient) : GameManager {
    companion object {
        fun create(game: Game, localPlayerPosition: PlayerPosition): RemoteGameManager {
            val gameView = GameView.forPlayer(game, localPlayerPosition)
            val gameService = LocalGameClient(game, localPlayerPosition)
            return RemoteGameManager(gameView, gameService)
        }
    }

    override suspend fun skip(): GameState {
        return if (isLocalPlayerTurn()) {
            val newGameView = gameClient.skip()
            val newManager = RemoteGameManager(newGameView, gameClient)

            if (newGameView.state != State.Ongoing)
                return ChooseAction(RemoteGameManager(newGameView, gameClient), RemoteGameUIData(newGameView))

            Wait(newManager, RemoteGameUIData(newGameView)) {
                flow { while (true) emit(newManager.gameClient.fetchStatus()) }
                    .filter { it.playerTurn == gameView.localPlayerPosition }
                    .map { ChooseAction(RemoteGameManager(it, gameClient), RemoteGameUIData(newGameView)) }
                    .first()
            }
        } else {
            ChooseAction(this, RemoteGameUIData(gameView), error = "Cannot manually skip for remote player")
        }
    }

    override suspend fun play(position: Position, cardId: String): GameState = if (isLocalPlayerTurn()) {
        val newGameView = gameClient.play(position, cardId)
        val newManager = RemoteGameManager(newGameView, gameClient)
        Wait(newManager, RemoteGameUIData(newGameView)) {
            flow { while (true) emit(newManager.gameClient.fetchStatus()) }
                .filter { it.playerTurn == gameView.localPlayerPosition }
                .map { ChooseAction(RemoteGameManager(it, gameClient), RemoteGameUIData(newGameView)) }
                .first()
        }
    } else {
        ChooseAction(this, RemoteGameUIData(gameView), error = "Cannot manually play for remote player")
    }

    override fun isPlayable(position: Position, cardId: String): Boolean =
        isLocalPlayerTurn() && gameClient.isPlayable(position, cardId)

    fun isLocalPlayerTurn(): Boolean = gameView.playerTurn == gameView.localPlayerPosition
}

/**
 * Service interface for game operations.
 * In real implementation, this would communicate with a server.
 */
interface GameClient {
    suspend fun skip(): GameView

    suspend fun play(position: Position, cardId: String): GameView

    suspend fun fetchStatus(): GameView

    fun isPlayable(position: Position, cardId: String): Boolean
}

/**
 * Local implementation of GameService for testing.
 * In real implementation, this would be replaced with server communication.
 */
class LocalGameClient(
    private var game: Game,
    private val localPlayerPosition: PlayerPosition
) : GameClient {
    override suspend fun skip(): GameView =
        when (val result = game.play(game.playerTurn, Action.Skip)) {
            is Success -> {
                game = result.value
                GameView.forPlayer(game, localPlayerPosition)
            }
            is Failure -> GameView.forPlayer(game, localPlayerPosition)
        }

    override suspend fun play(position: Position, cardId: String): GameView =
        when (val result = game.play(game.playerTurn, Play(position, cardId))) {
            is Success -> {
                game = result.value
                GameView.forPlayer(game, localPlayerPosition)
            }
            is Failure -> GameView.forPlayer(game, localPlayerPosition)
        }

    override suspend fun fetchStatus(): GameView {
        delay(800)
        return skip()
    }

    override fun isPlayable(position: Position, cardId: String): Boolean =
        game.play(game.playerTurn, Play(position, cardId)) is Success
}
