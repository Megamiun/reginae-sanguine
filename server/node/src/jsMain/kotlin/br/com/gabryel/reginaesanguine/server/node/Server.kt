package br.com.gabryel.reginaesanguine.server.node

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.parser.gameJsonParser
import br.com.gabryel.reginaesanguine.server.domain.ActionDto
import br.com.gabryel.reginaesanguine.server.domain.GameIdDto
import br.com.gabryel.reginaesanguine.server.domain.action.InitGameRequest
import br.com.gabryel.reginaesanguine.server.node.MigrationRunner.runMigrations
import br.com.gabryel.reginaesanguine.server.node.pg.createPool
import br.com.gabryel.reginaesanguine.server.node.repository.NodePackRepository
import br.com.gabryel.reginaesanguine.server.node.service.NodePackLoader
import br.com.gabryel.reginaesanguine.server.service.DeckService
import br.com.gabryel.reginaesanguine.server.service.GameService
import br.com.gabryel.reginaesanguine.server.service.PackSeederService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

val express = require("express")

/**
 * Creates and configures the Express application.
 * Can be used both for production and testing.
 */
fun createApp(
    deckService: DeckService,
    packSeederService: PackSeederService,
    gameService: GameService = GameService(deckService)
): dynamic {
    val app = express()
    val json = gameJsonParser()

    app.use(express.json())

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

    // Game routes
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
        "/deck/packs",
        handleRequestAsync { req: dynamic, res: dynamic ->
            val page = (req.query.page as? String)?.toIntOrNull() ?: 0
            val size = (req.query.size as? String)?.toIntOrNull() ?: 10
            val result = deckService.loadPacks(page, size)
            res.json(JSON.parse(json.encodeToString(result)))
        },
    )

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
            val seedResult = packSeederService.seedPacks()
            val result = js("{}")
            result.seeded = seedResult.seeded.toTypedArray()
            result.skipped = seedResult.skipped.toTypedArray()
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
    GlobalScope.launch {
        try {
            function(req, res)
        } catch (e: Throwable) {
            console.error("Error in async handler: ${e.message}")
            e.printStackTrace()
            val errorObj = js("{}")
            errorObj.error = e.message ?: "Unknown error"
            res.status(400).json(errorObj)
        } catch (e: dynamic) {
            console.error("Error in async handler (dynamic):", e)
            val errorObj = js("{}")
            errorObj.error = e.toString()
            res.status(400).json(errorObj)
        }
    }
}

suspend fun main() {
    val port = 3000

    val dbHost = js("process.env.DATABASE_HOST || 'localhost'") as String
    val dbPort = (js("parseInt(process.env.DATABASE_PORT) || 5432") as Number).toInt()
    val dbName = js("process.env.DATABASE_NAME || 'reginae_sanguine'") as String
    val dbUser = js("process.env.DATABASE_USER || 'postgres'") as String
    val dbPassword = js("process.env.DATABASE_PASSWORD || 'postgres'") as String

    runServer(dbHost, dbPort, dbName, dbUser, dbPassword, port)
}

suspend fun runServer(dbHost: String, dbPort: Int, dbName: String, dbUser: String, dbPassword: String, port: Int): dynamic {
    val pool = createPool(host = dbHost, port = dbPort, database = dbName, user = dbUser, password = dbPassword)

    runMigrations(pool)

    val packRepository = NodePackRepository(pool)
    val packLoader = NodePackLoader()
    val packSeederService = PackSeederService(packRepository, packLoader)
    val deckService = DeckService(packRepository)

    return createApp(deckService, packSeederService).listen(port) {
        console.log("Server running at http://localhost:$port")
        console.log("Database: $dbHost:$dbPort/$dbName")
    }
}
