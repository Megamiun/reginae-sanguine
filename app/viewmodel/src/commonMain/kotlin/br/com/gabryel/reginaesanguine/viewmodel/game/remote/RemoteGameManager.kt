package br.com.gabryel.reginaesanguine.viewmodel.game.remote

import br.com.gabryel.reginaesanguine.domain.PlayableMove
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.viewmodel.game.AwaitMatch
import br.com.gabryel.reginaesanguine.viewmodel.game.AwaitTurn
import br.com.gabryel.reginaesanguine.viewmodel.game.ChooseAction
import br.com.gabryel.reginaesanguine.viewmodel.game.GameClient
import br.com.gabryel.reginaesanguine.viewmodel.game.GameManager
import br.com.gabryel.reginaesanguine.viewmodel.game.GameState
import br.com.gabryel.reginaesanguine.viewmodel.game.dto.GameViewDto
import br.com.gabryel.reginaesanguine.viewmodel.game.dto.InitGameRequest
import br.com.gabryel.reginaesanguine.viewmodel.game.dto.StateDto
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
    private val currentStateData: RemoteGameStateData? = null
) : GameManager {
    companion object {
        suspend fun create(client: GameClient, request: InitGameRequest): RemoteGameManager {
            val gameId = client.initGame(request)
            return RemoteGameManager(client, gameId.gameId, request.position)
        }
    }

    fun awaitGameCreation(): GameState = AwaitMatch {
        flow { while (true) emit(gameClient.fetchStatus(gameId, playerPosition)) }
            .filterNotNull()
            .map(::awaitTurn)
            .first()
    }

    override suspend fun skip(): GameState =
        awaitTurn(gameClient.skip(gameId, playerPosition))

    override suspend fun play(position: Position, cardId: String): GameState =
        awaitTurn(gameClient.play(gameId, playerPosition, position, cardId))

    private fun awaitTurn(view: GameViewDto): GameState {
        val stateData = RemoteGameStateData(view)
        if (view.playerTurn == playerPosition || view.state !is StateDto.Ongoing)
            return ChooseAction(RemoteGameManager(gameClient, gameId, playerPosition, stateData), stateData)

        return AwaitTurn(stateData) {
            flow { while (true) emit(gameClient.fetchStatus(gameId, playerPosition)) }
                .filterNotNull()
                .filter { it.playerTurn == playerPosition }
                .map {
                    val newStateData = RemoteGameStateData(it)
                    ChooseAction(RemoteGameManager(gameClient, gameId, playerPosition, newStateData), newStateData)
                }
                .first()
        }
    }

    override fun getPlayableMoves(): Set<PlayableMove> =
        currentStateData?.playableMoves ?: emptySet()
}
