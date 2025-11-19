package br.com.gabryel.reginaesanguine.server.test

import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.server.client.ServerClient
import br.com.gabryel.reginaesanguine.server.client.get
import br.com.gabryel.reginaesanguine.server.client.post
import br.com.gabryel.reginaesanguine.server.client.put
import br.com.gabryel.reginaesanguine.server.domain.ActionDto
import br.com.gabryel.reginaesanguine.server.domain.ActionDto.Skip
import br.com.gabryel.reginaesanguine.server.domain.DeckDto
import br.com.gabryel.reginaesanguine.server.domain.GameRequestDto
import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatus
import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatusDto
import br.com.gabryel.reginaesanguine.server.domain.GameViewDto
import br.com.gabryel.reginaesanguine.server.domain.StateDto.Ongoing
import br.com.gabryel.reginaesanguine.server.domain.action.CreateAccountRequest
import br.com.gabryel.reginaesanguine.server.domain.action.CreateDeckRequest
import br.com.gabryel.reginaesanguine.server.domain.action.CreateGameRequestRequest
import br.com.gabryel.reginaesanguine.server.domain.action.JoinGameRequestRequest
import br.com.gabryel.reginaesanguine.server.domain.action.LoginRequest
import br.com.gabryel.reginaesanguine.server.domain.action.LoginResponse
import br.com.gabryel.reginaesanguine.server.domain.action.UpdateDeckRequest
import br.com.gabryel.reginaesanguine.server.domain.page.DeckPageDto
import br.com.gabryel.reginaesanguine.server.domain.page.GameRequestPageDto
import br.com.gabryel.reginaesanguine.server.domain.page.PackPageDto
import br.com.gabryel.reginaesanguine.server.service.SeedResult
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
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

        test("given account with deck, when creating game request, should return request with WAITING status") {
            val suffix = uniqueSuffix()
            val token = createTestAccountAndLogin(suffix)
            val deck = postCreateUserDeck(token, CreateDeckRequest("queens_blood", deckCardIds))

            val request = CreateGameRequestRequest(deckStateId = deck.stateId)
            val result = postCreateGameRequest(request, token)

            result.id.shouldNotBeBlank()
            result.creatorAccountId.shouldNotBeBlank()
            result.creatorDeckStateId shouldBe deck.stateId
            result.status shouldBe GameRequestStatus.WAITING
            result.gameId shouldBe null
            result.joinerAccountId shouldBe null
            result.joinerDeckStateId shouldBe null
        }

        test("given multiple game requests, when listing requests, should return paginated WAITING requests") {
            val suffix1 = uniqueSuffix()
            val token1 = createTestAccountAndLogin(suffix1)
            val deck1 = postCreateUserDeck(token1, CreateDeckRequest("queens_blood", deckCardIds))
            val request1 = postCreateGameRequest(CreateGameRequestRequest(deck1.stateId), token1)

            val suffix2 = uniqueSuffix()
            val token2 = createTestAccountAndLogin(suffix2)
            val deck2 = postCreateUserDeck(token2, CreateDeckRequest("queens_blood", deckCardIds))
            val request2 = postCreateGameRequest(CreateGameRequestRequest(deck2.stateId), token2)

            val result = getGameRequests(page = 0, size = 10)

            result.content.any { it.id == request1.id } shouldBe true
            result.content.any { it.id == request2.id } shouldBe true
            result.content.all { it.status == GameRequestStatus.WAITING } shouldBe true
            result.page shouldBe 0
            result.size shouldBe 10
        }

        test("given valid deck, when creating game, should return game ID") {
            val suffix1 = uniqueSuffix()
            val token1 = createTestAccountAndLogin(suffix1)
            val deck1 = postCreateUserDeck(token1, CreateDeckRequest("queens_blood", deckCardIds))
            val gameRequest = postCreateGameRequest(CreateGameRequestRequest(deck1.stateId), token1)

            val suffix2 = uniqueSuffix()
            val token2 = createTestAccountAndLogin(suffix2)
            val deck2 = postCreateUserDeck(token2, CreateDeckRequest("queens_blood", deckCardIds))
            val joinRequest = JoinGameRequestRequest(deckStateId = deck2.stateId)
            val joinResult = postJoinGameRequest(gameRequest.id, joinRequest, token2)

            joinResult.gameId.shouldNotBeBlank()
        }

        test("given created game, when fetching status, should return game view with initial state") {
            val suffix1 = uniqueSuffix()
            val token1 = createTestAccountAndLogin(suffix1)
            val deck1 = postCreateUserDeck(token1, CreateDeckRequest("queens_blood", deckCardIds))
            val gameRequest = postCreateGameRequest(CreateGameRequestRequest(deck1.stateId), token1)

            val suffix2 = uniqueSuffix()
            val token2 = createTestAccountAndLogin(suffix2)
            val deck2 = postCreateUserDeck(token2, CreateDeckRequest("queens_blood", deckCardIds))
            val joinRequest = JoinGameRequestRequest(deckStateId = deck2.stateId)
            val joinResult = postJoinGameRequest(gameRequest.id, joinRequest, token2)

            val gameId = joinResult.gameId.shouldNotBeNull()
            val gameView = getGameStatus(gameId, token1)

            gameView.packId shouldBe "queens_blood"
            gameView.localPlayerHand shouldHaveSize 5
            gameView.localPlayerDeckSize shouldBe 10
            gameView.playerTurn shouldBe LEFT
            gameView.state shouldBe Ongoing
            gameView.boardCells.size shouldBe 15
        }

        test("given created game, when player makes action, should update game state and switch turn") {
            val suffix1 = uniqueSuffix()
            val token1 = createTestAccountAndLogin(suffix1)
            val deck1 = postCreateUserDeck(token1, CreateDeckRequest("queens_blood", deckCardIds))
            val gameRequest = postCreateGameRequest(CreateGameRequestRequest(deck1.stateId), token1)

            val suffix2 = uniqueSuffix()
            val token2 = createTestAccountAndLogin(suffix2)
            val deck2 = postCreateUserDeck(token2, CreateDeckRequest("queens_blood", deckCardIds))
            val joinRequest = JoinGameRequestRequest(deckStateId = deck2.stateId)
            val joinResult = postJoinGameRequest(gameRequest.id, joinRequest, token2)

            val gameId = joinResult.gameId.shouldNotBeNull()
            val position = joinResult.myPosition.shouldNotBeNull()

            val firstTurnToken = when (position) {
                LEFT -> token1
                RIGHT -> token2
            }

            val result = postAction(gameId, Skip, firstTurnToken)

            result.playerTurn shouldBe RIGHT
            result.state shouldBe Ongoing
        }
    }

    suspend fun getPacks(page: Int = 0, size: Int = 10): PackPageDto =
        client.get("/pack?page=$page&size=$size")

    suspend fun getGameStatus(gameId: String, token: String): GameViewDto =
        client.get("/game/$gameId/status", mapOf("Authorization" to token))

    suspend fun postAction(gameId: String, action: ActionDto, token: String): GameViewDto =
        client.post("/game/$gameId/action", action, mapOf("Authorization" to token))

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

    suspend fun postCreateGameRequest(request: CreateGameRequestRequest, token: String): GameRequestDto =
        client.post("/game-request", request, mapOf("Authorization" to "Bearer $token"))

    suspend fun getGameRequests(page: Int = 0, size: Int = 10): GameRequestPageDto =
        client.get("/game-request?page=$page&size=$size")

    suspend fun postJoinGameRequest(
        requestId: String,
        request: JoinGameRequestRequest,
        token: String
    ): GameRequestStatusDto = client.post(
        "/game-request/$requestId/join",
        request,
        mapOf("Authorization" to "Bearer $token")
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
