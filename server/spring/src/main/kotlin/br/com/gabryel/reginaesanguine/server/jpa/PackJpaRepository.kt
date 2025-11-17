package br.com.gabryel.reginaesanguine.server.jpa

import br.com.gabryel.reginaesanguine.server.entity.PackEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PackJpaRepository : JpaRepository<PackEntity, UUID> {
    fun existsByAlias(alias: String): Boolean

    fun findByAlias(alias: String): PackEntity?
}
