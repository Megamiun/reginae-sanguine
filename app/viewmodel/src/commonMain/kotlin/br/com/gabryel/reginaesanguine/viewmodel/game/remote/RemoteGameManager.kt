package br.com.gabryel.reginaesanguine.viewmodel.game.remote

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.PlayableMove
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.server.domain.GameViewDto
import br.com.gabryel.reginaesanguine.server.domain.StateDto
import br.com.gabryel.reginaesanguine.viewmodel.game.AwaitMatch
import br.com.gabryel.reginaesanguine.viewmodel.game.AwaitTurn
import br.com.gabryel.reginaesanguine.viewmodel.game.ChooseAction
import br.com.gabryel.reginaesanguine.viewmodel.game.GameClient
import br.com.gabryel.reginaesanguine.viewmodel.game.GameEnd
import br.com.gabryel.reginaesanguine.viewmodel.game.GameManager
import br.com.gabryel.reginaesanguine.viewmodel.game.GameState
import br.com.gabryel.reginaesanguine.viewmodel.lobby.GameRequestClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

// TODO Fix all !! on class

/**
 * Remote game client that only exposes minimal GameView information.
 * Never exposes full Game objects to maintain security.
 *
 * - Local player: Actions are executed immediately via server
 * - Remote player: Waits for their turn
 */
class RemoteGameManager(
    private val gameClient: GameClient,
    private val gameRequestClient: GameRequestClient,
    private val gameRequestId: String,
    private val availableCards: Map<String, Card>,
    private val currentStateData: RemoteGameStateData? = null
) : GameManager {
    companion object {
        suspend fun create(
            client: GameClient,
            gameRequestClient: GameRequestClient,
            deckStateId: String,
            availableCards: Map<String, Card>
        ): RemoteGameManager {
            val available = gameRequestClient
                .listAvailable()
                .content
                .firstOrNull()

            if (available == null) {
                val gameRequest = gameRequestClient.create(deckStateId)
                return RemoteGameManager(client, gameRequestClient, gameRequest.id, availableCards)
            }

            gameRequestClient.join(available.id, deckStateId)

            return RemoteGameManager(
                client,
                gameRequestClient,
                available.id,
                availableCards,
            )
        }
    }

    fun awaitGameCreation(): GameState = AwaitMatch { callback ->
        val gameState = fetchGameStatus()
            .map(::awaitTurn)
            .first()
        callback(gameState)
    }

    override suspend fun skip(): GameState =
        currentStateData!!.run { awaitTurn(gameClient.skip(id)) }

    override suspend fun play(position: Position, cardId: String): GameState =
        currentStateData!!.run { awaitTurn(gameClient.play(id, position, cardId)) }

    private suspend fun awaitTurn(view: GameViewDto): GameState {
        val stateData = RemoteGameStateData(view, availableCards)

        if (view.state !is StateDto.Ongoing) return GameEnd(stateData)

        if (view.playerTurn == stateData.localPlayerPosition) {
            val manager = RemoteGameManager(gameClient, gameRequestClient, gameRequestId, availableCards, stateData)
            return ChooseAction(manager, stateData)
        }

        return AwaitTurn(stateData) { callback ->
            var previousStateData = stateData
            fetchGameStatus().collect { gameView ->
                val newStateData = RemoteGameStateData(gameView, availableCards)

                if (newStateData != previousStateData) {
                    val manager = RemoteGameManager(
                        gameClient,
                        gameRequestClient,
                        gameRequestId,
                        availableCards,
                        newStateData,
                    )

                    when {
                        gameView.state !is StateDto.Ongoing -> {
                            callback(GameEnd(newStateData))
                            return@collect
                        }

                        gameView.playerTurn == newStateData.localPlayerPosition -> {
                            callback(ChooseAction(manager, newStateData))
                            return@collect
                        }

                        else -> {
                            // Update the UI with new board state while still waiting
                            callback(AwaitTurn(newStateData) { callback })
                            previousStateData = newStateData
                        }
                    }
                }
            }
        }
    }

    override fun getPlayableMoves(): Set<PlayableMove> =
        currentStateData?.playableMoves ?: emptySet()

    private fun fetchGameStatus(pollingInterval: Long = 500): Flow<GameViewDto> =
        if (currentStateData == null) {
            flow {
                while (true) {
                    delay(pollingInterval)
                    emit(gameRequestClient.getStatus(gameRequestId)?.gameId)
                }
            }
                .filterNotNull()
                .mapNotNull { gameClient.fetchStatus(it) }
        } else {
            flow {
                while (true) {
                    delay(pollingInterval)
                    emit(gameClient.fetchStatus(currentStateData.id))
                }
            }.filterNotNull()
        }
}
