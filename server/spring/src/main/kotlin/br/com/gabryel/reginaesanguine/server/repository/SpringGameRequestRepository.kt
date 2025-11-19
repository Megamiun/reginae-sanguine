package br.com.gabryel.reginaesanguine.server.repository

import br.com.gabryel.reginaesanguine.server.domain.GameRequestDto
import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatus
import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatus.STARTED
import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatus.STARTING
import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatus.WAITING
import br.com.gabryel.reginaesanguine.server.domain.page.GenericPageDto
import br.com.gabryel.reginaesanguine.server.domain.page.PageDto
import br.com.gabryel.reginaesanguine.server.entity.GameRequestEntity
import br.com.gabryel.reginaesanguine.server.jpa.GameRequestJpaRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class SpringGameRequestRepository(
    private val jpaRepository: GameRequestJpaRepository,
) : GameRequestRepository {
    override suspend fun create(
        creatorAccountId: String,
        creatorDeckStateId: String,
    ): GameRequestDto {
        val entity = GameRequestEntity(
            creatorAccountId = UUID.fromString(creatorAccountId),
            creatorDeckStateId = UUID.fromString(creatorDeckStateId),
            status = WAITING,
        )
        return jpaRepository.save(entity).toDto()
    }

    override suspend fun findById(id: String): GameRequestDto? {
        return jpaRepository.findById(UUID.fromString(id))
            .orElse(null)
            ?.toDto()
    }

    override suspend fun findAllByStatus(status: GameRequestStatus, page: Int, size: Int): PageDto<GameRequestDto> {
        val dbPage = jpaRepository.findAllByStatus(status, Pageable.ofSize(size).withPage(page))

        return GenericPageDto(
            dbPage.content.map { it.toDto() },
            page,
            size,
            dbPage.totalElements,
            dbPage.totalPages,
        )
    }

    override suspend fun join(id: String, joinerAccountId: String, joinerDeckStateId: String): GameRequestDto {
        val entity = jpaRepository.findById(UUID.fromString(id))
            .orElseThrow { IllegalArgumentException("Game request $id not found") }

        // TODO Fail if not WAITING

        entity.status = STARTING
        entity.joinerAccountId = UUID.fromString(joinerAccountId)
        entity.joinerDeckStateId = UUID.fromString(
            joinerDeckStateId
        )
        return jpaRepository.save(entity).toDto()
    }

    override suspend fun associateGame(id: String, gameId: String): GameRequestDto {
        val entity = jpaRepository.findById(UUID.fromString(id))
            .orElseThrow { IllegalArgumentException("Game request $id not found") }

        // TODO Fail if not STARTING

        entity.status = STARTED
        entity.gameId = UUID.fromString(gameId)
        return jpaRepository.save(entity).toDto()
    }
}
