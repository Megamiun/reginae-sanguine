package br.com.gabryel.reginaesanguine.server.node

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.parser.gameJsonParser
import br.com.gabryel.reginaesanguine.server.domain.ActionDto
import br.com.gabryel.reginaesanguine.server.domain.GameIdDto
import br.com.gabryel.reginaesanguine.server.domain.action.InitGameRequest
import br.com.gabryel.reginaesanguine.server.service.DeckService
import br.com.gabryel.reginaesanguine.server.service.GameService

external fun require(module: String): dynamic

val express = require("express")

/**
 * Creates and configures the Express application.
 * Can be used both for production and testing.
 */
fun createApp(deckService: DeckService, gameService: GameService = GameService(deckService)): dynamic {
    val app = express()

    val json = gameJsonParser()

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
        handleRequestAsync { req: dynamic, res: dynamic ->
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
        handleRequestAsync { req: dynamic, res: dynamic ->
            val packId = req.params.packId as String
            val pack = deckService.loadPack(packId)
            if (pack != null) {
                res.json(JSON.parse(json.encodeToString(pack)))
            } else {
                res.status(404).json(js("{ error: 'Pack not found' }"))
            }
        },
    )

    // Admin routes
    app.post(
        "/admin/seed-packs",
        handleRequestAsync { req: dynamic, res: dynamic ->
            // TODO: Implement pack seeding with PackSeederService
            val result = js("{}")
            result.message = "Pack seeding endpoint created - implementation pending"
            res.json(result)
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

fun handleRequestAsync(function: suspend (dynamic, dynamic) -> Unit) = { req: dynamic, res: dynamic ->
    kotlinx.coroutines.GlobalScope.launch {
        try {
            function(req, res)
        } catch (e: Throwable) {
            val errorObj = js("{}")
            errorObj.error = e.message
            res.status(400).json(errorObj)
        }
    }
}

fun main() {
    val port = 3000

    // TODO: Replace with NodePackRepository when PostgreSQL implementation is ready
    val packRepository = object : br.com.gabryel.reginaesanguine.server.repository.PackRepository {
        override suspend fun savePack(
            pack: br.com.gabryel.reginaesanguine.domain.Pack
        ): Unit = throw UnsupportedOperationException("Node.js repository not yet implemented")

        override suspend fun packExists(packId: String): Boolean = throw UnsupportedOperationException(
            "Node.js repository not yet implemented",
        )

        override suspend fun findPack(packId: String): br.com.gabryel.reginaesanguine.domain.Pack? {
            // For now, load from JSON files (temporary until PostgreSQL is implemented)
            return null // TODO: Implement file-based loading
        }

        override suspend fun findAllPacks(): List<br.com.gabryel.reginaesanguine.domain.Pack> = throw UnsupportedOperationException(
            "Node.js repository not yet implemented",
        )
    }

    val deckService = DeckService(packRepository)
    val app = createApp(deckService)

    app.listen(port) {
        console.log("Server running at http://localhost:$port")
    }
}
