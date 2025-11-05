package br.com.gabryel.reginaesanguine.server.node

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.parser.gameJsonParser
import br.com.gabryel.reginaesanguine.server.domain.ActionDto
import br.com.gabryel.reginaesanguine.server.domain.GameIdDto
import br.com.gabryel.reginaesanguine.server.domain.action.InitGameRequest
import br.com.gabryel.reginaesanguine.server.node.service.NodeDeckService
import br.com.gabryel.reginaesanguine.server.service.GameService

external fun require(module: String): dynamic

val express = require("express")

/**
 * Creates and configures the Express application.
 * Can be used both for production and testing.
 */
fun createApp(): dynamic {
    val app = express()

    val json = gameJsonParser()
    val deckService = NodeDeckService(json)
    val gameService = GameService(deckService)

    // Middleware
    app.use(express.json())

    // CORS middleware
    app.use { req: dynamic, res: dynamic, next: dynamic ->
        res.header("Access-Control-Allow-Origin", "*")
        res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization")
        res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        if (req.method == "OPTIONS") {
            res.sendStatus(200)
        } else {
            next()
        }
    }

    app.post(
        "/game",
        handleRequest { req: dynamic, res: dynamic ->
            val requestBody = JSON.stringify(req.body)
            val request = json.decodeFromString<InitGameRequest>(requestBody)
            val gameId = gameService.initGame(request)
            val response = GameIdDto(gameId)
            res.json(JSON.parse(json.encodeToString(response)))
        },
    )

    app.post(
        "/game/:gameId/action",
        handleRequest { req: dynamic, res: dynamic ->
            val gameId = req.params.gameId as String
            val authorization = req.headers.authorization as String
            val playerPosition = PlayerPosition.valueOf(authorization)

            val actionBody = JSON.stringify(req.body)
            val actionDto = json.decodeFromString<ActionDto>(actionBody)

            val action = actionDto.toDomain()

            val result = gameService.executeAction(gameId, playerPosition, action)
            res.json(JSON.parse(json.encodeToString(result)))
        },
    )

    app.get(
        "/game/:gameId/status",
        handleRequest { req: dynamic, res: dynamic ->
            val gameId = req.params.gameId as String
            val authorization = req.headers.authorization as String
            val playerPosition = PlayerPosition.valueOf(authorization)

            val result = gameService.fetchStatus(gameId, playerPosition)
            if (result != null) {
                res.json(JSON.parse(json.encodeToString(result)))
            } else {
                res.status(404).json(js("{ error: 'Game not found' }"))
            }
        },
    )

    // Deck routes
    app.get(
        "/deck/pack/:packId",
        handleRequest { req: dynamic, res: dynamic ->
            val packId = req.params.packId as String
            val pack = deckService.getPack(packId)
            res.json(JSON.parse(json.encodeToString(pack)))
        },
    )

    return app
}

fun handleRequest(function: (dynamic, dynamic) -> Unit) = { req: dynamic, res: dynamic ->
    try {
        function(req, res)
    } catch (e: Throwable) {
        val errorObj = js("{}")
        errorObj.error = e.message
        res.status(400).json(errorObj)
    }
}

fun main() {
    val port = 3000
    val app = createApp()

    app.listen(port) {
        console.log("Server running at http://localhost:$port")
    }
}
