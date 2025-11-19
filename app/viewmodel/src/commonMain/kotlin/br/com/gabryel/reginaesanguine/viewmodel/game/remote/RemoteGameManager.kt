package br.com.gabryel.reginaesanguine.viewmodel.game.remote

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.PlayableMove
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.server.domain.GameViewDto
import br.com.gabryel.reginaesanguine.server.domain.StateDto
import br.com.gabryel.reginaesanguine.server.domain.action.InitGameRequest
import br.com.gabryel.reginaesanguine.viewmodel.game.AwaitMatch
import br.com.gabryel.reginaesanguine.viewmodel.game.AwaitTurn
import br.com.gabryel.reginaesanguine.viewmodel.game.ChooseAction
import br.com.gabryel.reginaesanguine.viewmodel.game.GameClient
import br.com.gabryel.reginaesanguine.viewmodel.game.GameManager
import br.com.gabryel.reginaesanguine.viewmodel.game.GameState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Remote game client that only exposes minimal GameView information.
 * Never exposes full Game objects to maintain security.
 *
 * - Local player: Actions are executed immediately via server
 * - Remote player: Waits for their turn
 */
class RemoteGameManager(
    private val gameClient: GameClient,
    private val gameId: String,
    private val playerPosition: PlayerPosition,
    private val availableCards: Map<String, Card>,
    private val currentStateData: RemoteGameStateData? = null
) : GameManager {
    companion object {
        suspend fun create(
            client: GameClient,
            request: InitGameRequest,
            availableCards: Map<String, Card>
        ): RemoteGameManager {
            val gameId = client.initGame(request)
            return RemoteGameManager(client, gameId.gameId, request.position, availableCards)
        }
    }

    fun awaitGameCreation(): GameState = AwaitMatch {
        fetchGameStatus()
            .map(::awaitTurn)
            .first()
    }

    override suspend fun skip(): GameState =
        awaitTurn(gameClient.skip(gameId, playerPosition))

    override suspend fun play(position: Position, cardId: String): GameState =
        awaitTurn(gameClient.play(gameId, playerPosition, position, cardId))

    private fun awaitTurn(view: GameViewDto): GameState {
        val stateData = RemoteGameStateData(view, availableCards)
        if (view.playerTurn == playerPosition || view.state !is StateDto.Ongoing)
            return ChooseAction(
                RemoteGameManager(gameClient, gameId, playerPosition, availableCards, stateData),
                stateData,
            )

        return AwaitTurn(stateData) {
            fetchGameStatus()
                .filter { it.playerTurn == playerPosition }
                .map {
                    val newStateData = RemoteGameStateData(it, availableCards)
                    val manager = RemoteGameManager(gameClient, gameId, playerPosition, availableCards, newStateData)
                    ChooseAction(manager, newStateData)
                }.first()
        }
    }

    override fun getPlayableMoves(): Set<PlayableMove> =
        currentStateData?.playableMoves ?: emptySet()

    private fun fetchGameStatus(pollingInterval: Long = 500): Flow<GameViewDto> = flow {
        while (true) {
            delay(pollingInterval)
            emit(gameClient.fetchStatus(gameId, playerPosition))
        }
    }.filterNotNull()
}
