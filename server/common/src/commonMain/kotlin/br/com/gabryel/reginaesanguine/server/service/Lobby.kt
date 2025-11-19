package br.com.gabryel.reginaesanguine.server.service

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.server.domain.GameRequestDto
import br.com.gabryel.reginaesanguine.server.domain.page.GameRequestPageDto
import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatus.STARTED
import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatus.WAITING
import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatusDto
import br.com.gabryel.reginaesanguine.server.domain.action.CreateGameRequestRequest
import br.com.gabryel.reginaesanguine.server.domain.action.InitGameRequest
import br.com.gabryel.reginaesanguine.server.domain.action.JoinGameRequestRequest
import br.com.gabryel.reginaesanguine.server.repository.GameRequestRepository
import kotlin.random.Random

class Lobby(
    private val gameService: GameService,
    private val gameRequestRepository: GameRequestRepository,
) {
    suspend fun createGameRequest(accountId: String, request: CreateGameRequestRequest): GameRequestDto {
        return gameRequestRepository.create(
            creatorAccountId = accountId,
            creatorDeckStateId = request.deckStateId,
        )
    }

    suspend fun listAvailableGameRequests(page: Int = 0, size: Int = 10) =
        gameRequestRepository.findAllByStatus(status = WAITING, page = page, size = size)
            .upcast(::GameRequestPageDto)

    suspend fun joinGameRequest(
        requestId: String,
        accountId: String,
        request: JoinGameRequestRequest
    ): GameRequestStatusDto {
        val gameRequest = requireNotNull(gameRequestRepository.findById(requestId)) {
            "Game request $requestId not found"
        }
        require(gameRequest.status == WAITING) {
            "Game request $requestId is not available for joining"
        }
        require(gameRequest.creatorAccountId != accountId) {
            "Cannot join your own game request"
        }

        val joinedRequest = gameRequestRepository.join(requestId, accountId, request.deckStateId)

        val (creatorPosition, joinerPosition) = randomizePositions()

        val initRequest = InitGameRequest(
            joinedRequest.creatorAccountId,
            joinedRequest.creatorDeckStateId,
            creatorPosition,
            accountId,
            request.deckStateId,
        )
        val creatorGameId = gameService.initGame(initRequest)
        gameRequestRepository.associateGame(requestId, creatorGameId)

        return GameRequestStatusDto(STARTED, creatorGameId, joinerPosition)
    }

    suspend fun getGameRequestStatus(requestId: String, accountId: String): GameRequestStatusDto? {
        val gameRequest = gameRequestRepository.findById(requestId) ?: return null
        if (gameRequest.creatorAccountId != accountId && gameRequest.joinerAccountId != accountId) return null

        if (gameRequest.status != STARTED) return GameRequestStatusDto(gameRequest.status)

        val gameId = requireNotNull(gameRequest.gameId) { "Game ID not set" }

        val gameView = gameService.fetchStatus(gameId, accountId) ?: return null

        return GameRequestStatusDto(gameRequest.status, gameRequest.gameId, gameView.localPlayerPosition)
    }

    private fun randomizePositions(): Pair<PlayerPosition, PlayerPosition> = if (Random.nextBoolean()) {
        Pair(LEFT, RIGHT)
    } else {
        Pair(RIGHT, LEFT)
    }
}
