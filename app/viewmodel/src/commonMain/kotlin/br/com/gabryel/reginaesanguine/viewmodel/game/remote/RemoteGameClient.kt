package br.com.gabryel.reginaesanguine.viewmodel.game.remote

import br.com.gabryel.reginaesanguine.domain.Action
import br.com.gabryel.reginaesanguine.server.client.ServerClient
import br.com.gabryel.reginaesanguine.server.client.get
import br.com.gabryel.reginaesanguine.server.client.post
import br.com.gabryel.reginaesanguine.server.domain.ActionDto
import br.com.gabryel.reginaesanguine.server.domain.GameViewDto
import br.com.gabryel.reginaesanguine.viewmodel.game.GameClient

/**
 * Remote implementation of GameClient that communicates with a server via HTTP.
 */
class RemoteGameClient(private val client: ServerClient, private val getToken: () -> String?) : GameClient {
    override suspend fun submitAction(gameId: String, action: Action<out String>): GameViewDto =
        client.post("game/$gameId/action", ActionDto.from(action), mapOf("Authorization" to getToken()!!))

    override suspend fun fetchStatus(gameId: String): GameViewDto =
        client.get("game/$gameId/status", mapOf("Authorization" to getToken()!!))
}
