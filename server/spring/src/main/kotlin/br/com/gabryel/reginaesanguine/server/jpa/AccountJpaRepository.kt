package br.com.gabryel.reginaesanguine.server.jpa

import br.com.gabryel.reginaesanguine.server.entity.AccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AccountJpaRepository : JpaRepository<AccountEntity, UUID> {
    fun findByUsername(username: String): AccountEntity?

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean
}
