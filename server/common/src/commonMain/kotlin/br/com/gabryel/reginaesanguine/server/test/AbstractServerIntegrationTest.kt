package br.com.gabryel.reginaesanguine.server.test

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.server.domain.ActionDto
import br.com.gabryel.reginaesanguine.server.domain.GameIdDto
import br.com.gabryel.reginaesanguine.server.domain.GameViewDto
import br.com.gabryel.reginaesanguine.server.domain.StateDto.Ongoing
import br.com.gabryel.reginaesanguine.server.domain.action.InitGameRequest
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
    override suspend fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)

        val result = seedPacks()
        println("Pack seeding result: seeded=${result.seeded}, skipped=${result.skipped}")
    }

    init {
        test("given valid deck, when creating game, should return game ID") {
            // Given
            val request = InitGameRequest(
                packId = "queens_blood",
                deckCardIds = listOf("001", "002", "003", "004", "005", "001", "002", "003", "004", "005"),
                position = LEFT,
            )

            // When
            val result = postInitGame(request, LEFT)

            // Then
            result.gameId.shouldNotBeBlank()
        }

        test("given created game, when fetching status, should return game view with initial state") {
            // Given
            val initRequest = InitGameRequest(
                packId = "queens_blood",
                deckCardIds = listOf("001", "002", "003", "004", "005", "001", "002", "003", "004", "005"),
                position = LEFT,
            )
            val gameId = postInitGame(initRequest, LEFT).gameId

            // When
            val gameView = getGameStatus(gameId, LEFT)

            // Then
            gameView.packId shouldBe "queens_blood"
            gameView.localPlayerPosition shouldBe LEFT
            gameView.localPlayerHand shouldHaveSize 5
            gameView.localPlayerDeckSize shouldBe 5
            gameView.playerTurn shouldBe LEFT
            gameView.state shouldBe Ongoing
            gameView.boardCells.size shouldBe 15
        }

        test("given created game, when player makes action, should update game state and switch turn") {
            // Given
            val initRequest = InitGameRequest(
                packId = "queens_blood",
                deckCardIds = listOf("001", "002", "003", "004", "005", "001", "002", "003", "004", "005"),
                position = LEFT,
            )
            val gameId = postInitGame(initRequest, LEFT).gameId
            val initialStatus = getGameStatus(gameId, LEFT)
            initialStatus.playerTurn shouldBe LEFT

            // When
            val result = postAction(gameId, LEFT, ActionDto.Skip)

            // Then
            result.playerTurn shouldBe RIGHT
            result.state shouldBe Ongoing
        }
    }

    abstract suspend fun seedPacks(): SeedResult

    abstract suspend fun postInitGame(request: InitGameRequest, playerPosition: PlayerPosition): GameIdDto

    abstract suspend fun getGameStatus(gameId: String, playerPosition: PlayerPosition): GameViewDto

    abstract suspend fun postAction(gameId: String, playerPosition: PlayerPosition, action: ActionDto): GameViewDto
}
