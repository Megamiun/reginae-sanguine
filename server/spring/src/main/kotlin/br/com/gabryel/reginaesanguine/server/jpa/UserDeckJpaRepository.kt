package br.com.gabryel.reginaesanguine.server.jpa

import br.com.gabryel.reginaesanguine.server.entity.UserDeckEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserDeckJpaRepository : JpaRepository<UserDeckEntity, UUID> {
    fun findByAccountId(accountId: UUID): List<UserDeckEntity>

    fun countByAccountId(accountId: UUID): Int

    fun existsByIdAndAccountId(id: UUID, accountId: UUID): Boolean
}
