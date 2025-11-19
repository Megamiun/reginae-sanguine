package br.com.gabryel.reginaesanguine.server.repository

import br.com.gabryel.reginaesanguine.server.domain.GameRequestDto
import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatus
import br.com.gabryel.reginaesanguine.server.domain.page.PageDto

interface GameRequestRepository {
    suspend fun create(creatorAccountId: String, creatorDeckStateId: String): GameRequestDto

    suspend fun findById(id: String): GameRequestDto?

    suspend fun findAllByStatus(status: GameRequestStatus, page: Int, size: Int): PageDto<GameRequestDto>

    suspend fun join(id: String, joinerAccountId: String, joinerDeckStateId: String): GameRequestDto

    suspend fun associateGame(id: String, gameId: String): GameRequestDto
}
