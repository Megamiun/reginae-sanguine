package br.com.gabryel.reginaesanguine.server.test

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.server.client.ServerClient
import br.com.gabryel.reginaesanguine.server.client.get
import br.com.gabryel.reginaesanguine.server.client.post
import br.com.gabryel.reginaesanguine.server.domain.ActionDto
import br.com.gabryel.reginaesanguine.server.domain.ActionDto.Skip
import br.com.gabryel.reginaesanguine.server.domain.GameIdDto
import br.com.gabryel.reginaesanguine.server.domain.GameViewDto
import br.com.gabryel.reginaesanguine.server.domain.StateDto.Ongoing
import br.com.gabryel.reginaesanguine.server.domain.action.InitGameRequest
import br.com.gabryel.reginaesanguine.server.dto.PackPageDto
import br.com.gabryel.reginaesanguine.server.service.SeedResult
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank

/**
 * Abstract test suite defining the contract for server implementations.
 * Both Spring and Node.js servers should extend this and implement the HTTP client methods.
 *
 * Uses Kotest FunSpec for consistent test structure across platforms.
 *
 * TODO Move to a test module later
 */
abstract class AbstractServerIntegrationTest : FunSpec() {
    protected abstract var client: ServerClient

    override suspend fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)

        val result = seedPacks()
        println("Pack seeding result: seeded=${result.seeded}, skipped=${result.skipped}")
    }

    init {
        test("given seeded packs, when getting paginated packs, should return page with packs") {
            val page = getPacks(page = 0, size = 10)

            page.page shouldBe 0
            page.size shouldBe 10
            page.totalElements shouldBe 1L
            page.totalPages shouldBe 1
            page.content shouldHaveSize 1
            page.content[0].name shouldBe "Standard"
            page.content[0].cards shouldHaveSize 166
        }

        test("given valid deck, when creating game, should return game ID") {
            val request = InitGameRequest(
                packId = "queens_blood",
                deckCardIds = listOf(
                    "001",
                    "002",
                    "003",
                    "004",
                    "005",
                    "001",
                    "002",
                    "003",
                    "004",
                    "005",
                ),
                position = LEFT,
            )

            val result = postInitGame(request, LEFT)

            result.gameId.shouldNotBeBlank()
        }

        test("given created game, when fetching status, should return game view with initial state") {
            val initRequest = InitGameRequest(
                packId = "queens_blood",
                deckCardIds = listOf(
                    "001",
                    "002",
                    "003",
                    "004",
                    "005",
                    "001",
                    "002",
                    "003",
                    "004",
                    "005",
                ),
                position = LEFT,
            )
            val gameId = postInitGame(initRequest, LEFT).gameId

            val gameView = getGameStatus(gameId, LEFT)

            gameView.packId shouldBe "queens_blood"
            gameView.localPlayerPosition shouldBe LEFT
            gameView.localPlayerHand shouldHaveSize 5
            gameView.localPlayerDeckSize shouldBe 5
            gameView.playerTurn shouldBe LEFT
            gameView.state shouldBe Ongoing
            gameView.boardCells.size shouldBe 15
        }

        test("given created game, when player makes action, should update game state and switch turn") {
            val initRequest = InitGameRequest(
                packId = "queens_blood",
                deckCardIds = listOf(
                    "001",
                    "002",
                    "003",
                    "004",
                    "005",
                    "001",
                    "002",
                    "003",
                    "004",
                    "005",
                ),
                position = LEFT,
            )
            val gameId = postInitGame(initRequest, LEFT).gameId
            val initialStatus = getGameStatus(gameId, LEFT)
            initialStatus.playerTurn shouldBe LEFT

            val result = postAction(gameId, LEFT, Skip)

            result.playerTurn shouldBe RIGHT
            result.state shouldBe Ongoing
        }
    }

    suspend fun getPacks(page: Int = 0, size: Int = 10): PackPageDto =
        client.get("/deck/packs?page=$page&size=$size")

    suspend fun postInitGame(request: InitGameRequest, playerPosition: PlayerPosition): GameIdDto =
        client.post("/game", request, mapOf("Authorization" to playerPosition.name))

    suspend fun getGameStatus(gameId: String, playerPosition: PlayerPosition): GameViewDto =
        client.get("/game/$gameId/status", mapOf("Authorization" to playerPosition.name))

    suspend fun postAction(
        gameId: String,
        playerPosition: PlayerPosition,
        action: ActionDto
    ): GameViewDto =
        client.post("/game/$gameId/action", action, mapOf("Authorization" to playerPosition.name))

    suspend fun seedPacks(): SeedResult = client.post("/admin/seed-packs", null)
}
