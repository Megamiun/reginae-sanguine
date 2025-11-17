package br.com.gabryel.reginaesanguine.server.node

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.parser.gameJsonParser
import br.com.gabryel.reginaesanguine.server.domain.ActionDto
import br.com.gabryel.reginaesanguine.server.domain.GameIdDto
import br.com.gabryel.reginaesanguine.server.domain.action.CreateAccountRequest
import br.com.gabryel.reginaesanguine.server.domain.action.CreateDeckRequest
import br.com.gabryel.reginaesanguine.server.domain.action.InitGameRequest
import br.com.gabryel.reginaesanguine.server.domain.action.LoginRequest
import br.com.gabryel.reginaesanguine.server.domain.action.UpdateDeckRequest
import br.com.gabryel.reginaesanguine.server.node.MigrationRunner.runMigrations
import br.com.gabryel.reginaesanguine.server.node.pg.createPool
import br.com.gabryel.reginaesanguine.server.node.repository.NodeAccountDeckRepository
import br.com.gabryel.reginaesanguine.server.node.repository.NodeAccountRepository
import br.com.gabryel.reginaesanguine.server.node.repository.NodePackRepository
import br.com.gabryel.reginaesanguine.server.node.service.NodePackLoader
import br.com.gabryel.reginaesanguine.server.service.AccountDeckService
import br.com.gabryel.reginaesanguine.server.service.AccountService
import br.com.gabryel.reginaesanguine.server.service.DeckService
import br.com.gabryel.reginaesanguine.server.service.GameService
import br.com.gabryel.reginaesanguine.server.service.PackSeederService
import br.com.gabryel.reginaesanguine.server.service.security.Bcrypt
import br.com.gabryel.reginaesanguine.server.service.security.JwtService
import br.com.gabryel.reginaesanguine.server.service.security.TokenService
import kotlinx.coroutines.DelicateCoroutinesApi
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
    accountService: AccountService,
    accountDeckService: AccountDeckService,
    tokenService: TokenService,
    gameService: GameService,
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

    // Account routes
    app.post(
        "/account",
        handleRequestAsync { req: dynamic, res: dynamic ->
            val requestBody = JSON.stringify(req.body)
            val request = json.decodeFromString<CreateAccountRequest>(requestBody)
            val accountDto = accountService.create(request)
            res.status(201).json(JSON.parse(json.encodeToString(accountDto)))
        },
    )

    app.post(
        "/account/login",
        handleRequestAsync { req: dynamic, res: dynamic ->
            val requestBody = JSON.stringify(req.body)
            val request = json.decodeFromString<LoginRequest>(requestBody)
            val loginResponse = accountService.login(request)
            res.json(JSON.parse(json.encodeToString(loginResponse)))
        },
    )

    // User deck routes
    app.post(
        "/user-deck",
        handleRequestAsync { req: dynamic, res: dynamic ->
            val accountId = extractAccountId(req, tokenService, res) ?: return@handleRequestAsync
            val requestBody = JSON.stringify(req.body)
            val request = json.decodeFromString<CreateDeckRequest>(requestBody)
            val deckDto = accountDeckService.create(accountId, request)
            res.status(201).json(JSON.parse(json.encodeToString(deckDto)))
        },
    )

    app.get(
        "/user-deck",
        handleRequestAsync { req: dynamic, res: dynamic ->
            val accountId = extractAccountId(req, tokenService, res) ?: return@handleRequestAsync
            val decks = accountDeckService.getAllByAccountId(accountId)
            res.json(JSON.parse(json.encodeToString(decks)))
        },
    )

    app.get(
        "/user-deck/:deckId",
        handleRequestAsync { req: dynamic, res: dynamic ->
            val accountId = extractAccountId(req, tokenService, res) ?: return@handleRequestAsync
            val deckId = req.params.deckId as String
            val deck = accountDeckService.getById(accountId, deckId)
            res.json(JSON.parse(json.encodeToString(deck)))
        },
    )

    app.put(
        "/user-deck/:deckId",
        handleRequestAsync { req: dynamic, res: dynamic ->
            val accountId = extractAccountId(req, tokenService, res) ?: return@handleRequestAsync
            val deckId = req.params.deckId as String
            val requestBody = JSON.stringify(req.body)
            val request = json.decodeFromString<UpdateDeckRequest>(requestBody)
            val deckDto = accountDeckService.update(accountId, deckId, request)
            res.json(JSON.parse(json.encodeToString(deckDto)))
        },
    )

    return app
}

private fun extractAccountId(req: dynamic, tokenService: TokenService, res: dynamic): String? {
    val authorization = req.headers.authorization as? String
    if (authorization == null) {
        val errorObj = js("{}")
        errorObj.error = "Authorization header required"
        res.status(401).json(errorObj)
        return null
    }
    val token = authorization.removePrefix("Bearer ").trim()
    val accountId = tokenService.validateToken(token)
    if (accountId == null) {
        val errorObj = js("{}")
        errorObj.error = "Invalid or expired token"
        res.status(401).json(errorObj)
        return null
    }
    return accountId
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

@OptIn(DelicateCoroutinesApi::class)
fun handleRequestAsync(function: suspend (dynamic, dynamic) -> Unit) = { req: dynamic, res: dynamic ->
    GlobalScope.launch {
        try {
            function(req, res)
        } catch (e: Throwable) {
            console.error("Error in async handler: ${e.message}", e)
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
    val jwtPrivateKey = js("process.env.JWT_PRIVATE_KEY") as? String ?: loadTestPrivateKey()
    val jwtPublicKey = js("process.env.JWT_PUBLIC_KEY") as? String ?: loadTestPublicKey()

    runServer(dbHost, dbPort, dbName, dbUser, dbPassword, jwtPrivateKey, jwtPublicKey, port)
}

private fun loadTestPrivateKey(): String = loadResource("jwt/private.pem")

private fun loadTestPublicKey(): String = loadResource("jwt/public.pem")

private fun loadResource(path: String): String {
    val fs = js("require('fs')")
    val pathModule = js("require('path')")
    val resourcePath = pathModule.join(js("__dirname"), path) as String
    return fs.readFileSync(resourcePath, "utf-8") as String
}

suspend fun runServer(
    dbHost: String,
    dbPort: Int,
    dbName: String,
    dbUser: String,
    dbPassword: String,
    jwtPrivateKey: String,
    jwtPublicKey: String,
    port: Int,
): dynamic {
    val pool = createPool(host = dbHost, port = dbPort, database = dbName, user = dbUser, password = dbPassword)

    runMigrations(pool)

    val packRepository = NodePackRepository(pool)
    val accountRepository = NodeAccountRepository(pool)
    val accountDeckRepository = NodeAccountDeckRepository(pool)
    val packLoader = NodePackLoader()
    val packSeederService = PackSeederService(packRepository, packLoader)
    val deckService = DeckService(packRepository)
    val passwordHasher = Bcrypt()
    val tokenService = JwtService(jwtPrivateKey, jwtPublicKey)
    val accountService = AccountService(accountRepository, passwordHasher, tokenService)
    val accountDeckService = AccountDeckService(accountDeckRepository)
    val gameService = GameService(deckService, accountDeckRepository)

    return createApp(
        deckService,
        packSeederService,
        accountService,
        accountDeckService,
        tokenService,
        gameService,
    ).listen(port) {
        console.log("Server running at http://localhost:$port")
        console.log("Database: $dbHost:$dbPort/$dbName")
    }
}
