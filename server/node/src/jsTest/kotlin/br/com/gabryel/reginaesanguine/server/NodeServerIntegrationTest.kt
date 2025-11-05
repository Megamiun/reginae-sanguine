package br.com.gabryel.reginaesanguine.server

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.parser.gameJsonParser
import br.com.gabryel.reginaesanguine.server.domain.ActionDto
import br.com.gabryel.reginaesanguine.server.domain.GameIdDto
import br.com.gabryel.reginaesanguine.server.domain.GameViewDto
import br.com.gabryel.reginaesanguine.server.domain.action.InitGameRequest
import br.com.gabryel.reginaesanguine.server.node.createApp
import br.com.gabryel.reginaesanguine.server.test.AbstractServerIntegrationTest
import io.kotest.common.KotestInternal
import io.kotest.core.log
import io.kotest.core.spec.Spec
import kotlinx.coroutines.await
import kotlinx.coroutines.delay
import kotlin.js.Promise

@JsModule("node-fetch")
@JsNonModule
external fun fetch(url: String, options: dynamic = definedExternally): Promise<dynamic>

/**
 * Node.js integration test implementation using Kotest FunSpec.
 * Embeds the Express server in-process for testing.
 *
 * Run with: ./gradlew :server:node:jsTest
 */
@OptIn(KotestInternal::class)
class NodeServerIntegrationTest : AbstractServerIntegrationTest() {
    private var server: dynamic = null
    private val testPort = 3001
    private val baseUrl = "http://localhost:$testPort"
    private val json = gameJsonParser()

    override suspend fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        val app = createApp()
        server = app.listen(testPort)
        server?.unref()
    }

    override suspend fun afterSpec(spec: Spec) {
        log { "Closing server..." }
        server?.close()
        server = null
        log { "Server closed" }
        super.afterSpec(spec)
    }

    override suspend fun postInitGame(request: InitGameRequest, playerPosition: PlayerPosition): GameIdDto {
        val requestJson = json.encodeToString<InitGameRequest>(request)
        val options = js("{}")
        options.method = "POST"
        options.headers = js("{}")
        options.headers["Content-Type"] = "application/json"
        options.headers.Authorization = playerPosition.name
        options.body = requestJson

        val response = fetch("$baseUrl/game", options).await()
        val body = (response.json() as Promise<dynamic>).await()

        if (!response.ok) {
            throw IllegalStateException("Failed to init game: ${response.status} - ${JSON.stringify(body)}")
        }

        val bodyStr = JSON.stringify(body)
        return json.decodeFromString<GameIdDto>(bodyStr)
    }

    override suspend fun getGameStatus(gameId: String, playerPosition: PlayerPosition): GameViewDto {
        val options = js("{}")
        options.headers = js("{}")
        options.headers.Authorization = playerPosition.name

        val response = fetch("$baseUrl/game/$gameId/status", options).await()
        val body = (response.json() as Promise<dynamic>).await()

        if (!response.ok) {
            throw IllegalStateException("Failed to get game status: ${response.status} - ${JSON.stringify(body)}")
        }

        val bodyStr = JSON.stringify(body)
        return json.decodeFromString<GameViewDto>(bodyStr)
    }

    override suspend fun postAction(
        gameId: String,
        playerPosition: PlayerPosition,
        action: ActionDto
    ): GameViewDto {
        val actionJson = json.encodeToString<ActionDto>(action)
        val options = js("{}")
        options.method = "POST"
        options.headers = js("{}")
        options.headers["Content-Type"] = "application/json"
        options.headers.Authorization = playerPosition.name
        options.body = actionJson

        val response = fetch("$baseUrl/game/$gameId/action", options).await()
        val body = (response.json() as Promise<dynamic>).await()

        if (!response.ok) {
            throw IllegalStateException("Failed to execute action: ${response.status} - ${JSON.stringify(body)}")
        }

        val bodyStr = JSON.stringify(body)
        return json.decodeFromString<GameViewDto>(bodyStr)
    }
}
