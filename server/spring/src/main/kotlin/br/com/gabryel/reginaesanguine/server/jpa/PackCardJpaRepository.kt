package br.com.gabryel.reginaesanguine.server.jpa

import br.com.gabryel.reginaesanguine.server.entity.PackCardEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PackCardJpaRepository : JpaRepository<PackCardEntity, UUID> {
    fun findByPackId(packId: UUID): List<PackCardEntity>
}
