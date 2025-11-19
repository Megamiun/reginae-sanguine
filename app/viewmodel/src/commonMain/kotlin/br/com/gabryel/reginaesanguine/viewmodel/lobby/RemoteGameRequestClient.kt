package br.com.gabryel.reginaesanguine.viewmodel.lobby

import br.com.gabryel.reginaesanguine.server.client.ServerClient
import br.com.gabryel.reginaesanguine.server.client.get
import br.com.gabryel.reginaesanguine.server.client.post
import br.com.gabryel.reginaesanguine.server.domain.GameRequestDto
import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatusDto
import br.com.gabryel.reginaesanguine.server.domain.action.CreateGameRequestRequest
import br.com.gabryel.reginaesanguine.server.domain.action.JoinGameRequestRequest
import br.com.gabryel.reginaesanguine.server.domain.page.GameRequestPageDto

class RemoteGameRequestClient(
    private val serverClient: ServerClient,
    private val getToken: () -> String?,
) : GameRequestClient {
    private val headers: Map<String, String>
        get() {
            val token = getToken() ?: error("No authentication token available")
            return mapOf("Authorization" to "Bearer $token")
        }

    override suspend fun create(deckStateId: String): GameRequestDto =
        serverClient.post("game-request", CreateGameRequestRequest(deckStateId), headers)

    override suspend fun listAvailable(): GameRequestPageDto =
        serverClient.get("game-request")

    override suspend fun join(gameRequestId: String, deckStateId: String): GameRequestStatusDto =
        serverClient.post("game-request/$gameRequestId/join", JoinGameRequestRequest(deckStateId), headers)

    override suspend fun getStatus(gameRequestId: String): GameRequestStatusDto? =
        serverClient.get("game-request/$gameRequestId/status", headers)
}
