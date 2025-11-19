package br.com.gabryel.reginaesanguine.server.node.repository

import br.com.gabryel.reginaesanguine.server.domain.GameRequestDto
import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatus
import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatus.WAITING
import br.com.gabryel.reginaesanguine.server.domain.page.GenericPageDto
import br.com.gabryel.reginaesanguine.server.domain.page.PageDto
import br.com.gabryel.reginaesanguine.server.repository.GameRequestRepository
import kotlin.math.ceil
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class NodeLocalGameRequestRepository : GameRequestRepository {
    private val requests = mutableMapOf<String, GameRequestDto>()

    override suspend fun create(creatorAccountId: String, creatorDeckStateId: String): GameRequestDto {
        val newRequest = GameRequestDto(Uuid.random().toString(), creatorAccountId, creatorDeckStateId, WAITING)
        requests += newRequest.id to newRequest

        return newRequest
    }

    override suspend fun join(id: String, joinerAccountId: String, joinerDeckStateId: String): GameRequestDto {
        val requestToJoin = requests.entries.first { it.key == id }.value

        val updatedRequest =
            requestToJoin.copy(joinerAccountId = joinerAccountId, joinerDeckStateId = joinerDeckStateId)
        requests += updatedRequest.id to updatedRequest

        return updatedRequest
    }

    override suspend fun associateGame(id: String, gameId: String): GameRequestDto {
        val requestToJoin = requests.entries.first { it.key == id }.value

        val updatedRequest = requestToJoin.copy(gameId = gameId)
        requests += updatedRequest.id to updatedRequest

        return updatedRequest
    }

    override suspend fun findById(id: String): GameRequestDto? = requests.entries.firstOrNull { it.key == id }?.value

    override suspend fun findAllByStatus(
        status: GameRequestStatus,
        page: Int,
        size: Int
    ): PageDto<GameRequestDto> {
        val offset = page * size
        val byStatus = requests.entries
            .filter { it.value.status == status }
            .map { it.value }

        val result = byStatus
            .drop(offset)
            .take(size)

        val totalElements = byStatus.size.toLong()

        return GenericPageDto(
            result,
            page,
            size,
            totalElements,
            ceil(totalElements.toDouble() / size).toInt(),
        )
    }
}
