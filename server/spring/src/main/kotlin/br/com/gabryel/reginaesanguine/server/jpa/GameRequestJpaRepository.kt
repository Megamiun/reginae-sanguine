package br.com.gabryel.reginaesanguine.server.jpa

import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatus
import br.com.gabryel.reginaesanguine.server.entity.GameRequestEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface GameRequestJpaRepository : JpaRepository<GameRequestEntity, UUID> {
    fun findAllByStatus(status: GameRequestStatus, pageable: Pageable): Page<GameRequestEntity>
}
