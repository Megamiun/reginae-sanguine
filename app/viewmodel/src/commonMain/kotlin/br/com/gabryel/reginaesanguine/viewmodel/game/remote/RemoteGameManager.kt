package br.com.gabryel.reginaesanguine.viewmodel.game.remote

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.GameView
import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.viewmodel.game.AwaitMatch
import br.com.gabryel.reginaesanguine.viewmodel.game.AwaitTurn
import br.com.gabryel.reginaesanguine.viewmodel.game.ChooseAction
import br.com.gabryel.reginaesanguine.viewmodel.game.GameClient
import br.com.gabryel.reginaesanguine.viewmodel.game.GameManager
import br.com.gabryel.reginaesanguine.viewmodel.game.GameState
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
 * - Remote player: Waits 2 seconds then automatically skips
 */
class RemoteGameManager(
    private val gameClient: GameClient,
    private val gameId: String,
    private val playerPosition: PlayerPosition
) : GameManager {
    companion object {
        suspend fun create(deck: List<Card>, position: PlayerPosition, pack: Pack): RemoteGameManager {
            val client = LocalGameClient(400)
            val gameId = client.initGame(deck, position, pack)
            return RemoteGameManager(client, gameId, position)
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

    private fun awaitTurn(view: GameView): GameState {
        if (view.playerTurn == playerPosition)
            return ChooseAction(this, RemoteGameStateData(view))

        return AwaitTurn(RemoteGameStateData(view)) {
            flow { while (true) emit(gameClient.fetchStatus(gameId, playerPosition)) }
                .filterNotNull()
                .filter { it.playerTurn == playerPosition }
                .map { ChooseAction(this, RemoteGameStateData(it)) }
                .first()
        }
    }

    override fun isPlayable(position: Position, cardId: String): Boolean =
        gameClient.isPlayable(gameId, playerPosition, position, cardId)
}
