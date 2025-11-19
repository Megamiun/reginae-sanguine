package br.com.gabryel.reginaesanguine.server.test

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.server.client.ServerClient
import br.com.gabryel.reginaesanguine.server.client.get
import br.com.gabryel.reginaesanguine.server.client.post
import br.com.gabryel.reginaesanguine.server.client.put
import br.com.gabryel.reginaesanguine.server.domain.ActionDto
import br.com.gabryel.reginaesanguine.server.domain.ActionDto.Skip
import br.com.gabryel.reginaesanguine.server.domain.DeckDto
import br.com.gabryel.reginaesanguine.server.domain.DeckPageDto
import br.com.gabryel.reginaesanguine.server.domain.GameIdDto
import br.com.gabryel.reginaesanguine.server.domain.GameViewDto
import br.com.gabryel.reginaesanguine.server.domain.PackPageDto
import br.com.gabryel.reginaesanguine.server.domain.StateDto.Ongoing
import br.com.gabryel.reginaesanguine.server.domain.action.CreateAccountRequest
import br.com.gabryel.reginaesanguine.server.domain.action.CreateDeckRequest
import br.com.gabryel.reginaesanguine.server.domain.action.InitGameRequest
import br.com.gabryel.reginaesanguine.server.domain.action.LoginRequest
import br.com.gabryel.reginaesanguine.server.domain.action.LoginResponse
import br.com.gabryel.reginaesanguine.server.domain.action.UpdateDeckRequest
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

    private var testCounter = 0

    override suspend fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)

        val result = seedPacks()
        println("Pack seeding result: seeded=${result.seeded}, skipped=${result.skipped}")
    }

    private fun uniqueSuffix(): String = "${++testCounter}}"

    init {
        val deckCardIds = List(15) { index -> "00${(index % 9) + 1}" }

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
            val suffix = uniqueSuffix()
            val token = createTestAccountAndLogin(suffix)
            val deck = postCreateUserDeck(token, CreateDeckRequest("queens_blood", deckCardIds))
            val request = InitGameRequest(deckStateId = deck.stateId, position = LEFT)

            val result = postInitGame(request, LEFT)

            result.gameId.shouldNotBeBlank()
        }

        test("given created game, when fetching status, should return game view with initial state") {
            val suffix = uniqueSuffix()
            val token = createTestAccountAndLogin(suffix)
            val deck = postCreateUserDeck(token, CreateDeckRequest("queens_blood", deckCardIds))
            val initRequest = InitGameRequest(deckStateId = deck.stateId, position = LEFT)
            val gameId = postInitGame(initRequest, LEFT).gameId

            val gameView = getGameStatus(gameId, LEFT)

            gameView.packId shouldBe "queens_blood"
            gameView.localPlayerPosition shouldBe LEFT
            gameView.localPlayerHand shouldHaveSize 5
            gameView.localPlayerDeckSize shouldBe 10
            gameView.playerTurn shouldBe LEFT
            gameView.state shouldBe Ongoing
            gameView.boardCells.size shouldBe 15
        }

        test("given created game, when player makes action, should update game state and switch turn") {
            val suffix = uniqueSuffix()
            val token = createTestAccountAndLogin(suffix)
            val deck = postCreateUserDeck(token, CreateDeckRequest("queens_blood", deckCardIds))
            val initRequest = InitGameRequest(deckStateId = deck.stateId, position = LEFT)
            val gameId = postInitGame(initRequest, LEFT).gameId
            val initialStatus = getGameStatus(gameId, LEFT)
            initialStatus.playerTurn shouldBe LEFT

            val result = postAction(gameId, LEFT, Skip)

            result.playerTurn shouldBe RIGHT
            result.state shouldBe Ongoing
        }

        test("given valid account data, when creating account, should return account with ID") {
            val suffix = uniqueSuffix()
            val request = CreateAccountRequest(
                username = "testuser_$suffix",
                email = "test_$suffix@example.com",
                password = "password123",
            )

            val result = postCreateAccount(request)

            result.account.id.shouldNotBeBlank()
            result.account.username shouldBe request.username
            result.account.email shouldBe request.email
            result.token.shouldNotBeBlank()
        }

        test("given created account, when logging in with correct credentials, should return token") {
            val suffix = uniqueSuffix()
            val username = "logintest_$suffix"
            val email = "login_$suffix@example.com"
            val password = "securepass"

            val createRequest = CreateAccountRequest(username, email, password)
            postCreateAccount(createRequest)

            val loginRequest = LoginRequest(username, password)
            val result = postLogin(loginRequest)

            result.token.shouldNotBeBlank()
            result.account.username shouldBe username
            result.account.email shouldBe email
        }

        test("given account, when creating deck with 15 cards, should return deck") {
            val suffix = uniqueSuffix()
            val token = createTestAccountAndLogin(suffix)

            val request = CreateDeckRequest(packAlias = "queens_blood", cardIds = deckCardIds)

            val result = postCreateUserDeck(token, request)

            result.id.shouldNotBeBlank()
            result.packId shouldBe "queens_blood"
            result.cardIds shouldBe deckCardIds
        }

        test("given account with deck, when getting all decks, should return list with deck") {
            val suffix = uniqueSuffix()
            val token = createTestAccountAndLogin(suffix)
            val deck = postCreateUserDeck(token, CreateDeckRequest("queens_blood", deckCardIds))

            val result = getUserDecks(token).content

            result shouldHaveSize 1
            result[0].id shouldBe deck.id
            result[0].cardIds shouldBe deckCardIds
        }

        test("given deck, when updating cards, should return updated deck") {
            val suffix = uniqueSuffix()
            val token = createTestAccountAndLogin(suffix)
            val deck = postCreateUserDeck(token, CreateDeckRequest("queens_blood", deckCardIds))

            val newCardIds = List(15) { "003" }
            val updateRequest = UpdateDeckRequest(newCardIds)
            val result = putUpdateUserDeck(token, deck.id, updateRequest)

            result.cardIds shouldBe newCardIds
        }
    }

    suspend fun getPacks(page: Int = 0, size: Int = 10): PackPageDto =
        client.get("/pack?page=$page&size=$size")

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

    suspend fun postCreateAccount(request: CreateAccountRequest): LoginResponse =
        client.post("/account", request)

    suspend fun postLogin(request: LoginRequest): LoginResponse =
        client.post("/account/login", request)

    suspend fun postCreateUserDeck(token: String, request: CreateDeckRequest): DeckDto =
        client.post("/account-deck", request, mapOf("Authorization" to "Bearer $token"))

    suspend fun getUserDecks(token: String): DeckPageDto =
        client.get("/account-deck", mapOf("Authorization" to "Bearer $token"))

    suspend fun putUpdateUserDeck(
        token: String,
        deckId: String,
        request: UpdateDeckRequest,
    ): DeckDto = client.put<UpdateDeckRequest, DeckDto>(
        "/account-deck/$deckId",
        request,
        mapOf("Authorization" to "Bearer $token"),
    )

    private suspend fun createTestAccountAndLogin(suffix: String): String {
        val username = "decktest_$suffix"
        val password = "password123"
        postCreateAccount(
            CreateAccountRequest(
                username = username,
                email = "deck_$suffix@example.com",
                password = password,
            ),
        )
        val loginResponse = postLogin(LoginRequest(username, password))
        return loginResponse.token
    }
}
