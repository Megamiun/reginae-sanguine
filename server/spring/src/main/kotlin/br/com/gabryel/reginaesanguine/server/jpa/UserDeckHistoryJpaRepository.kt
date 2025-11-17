package br.com.gabryel.reginaesanguine.server.jpa

import br.com.gabryel.reginaesanguine.server.entity.UserDeckStateEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserDeckHistoryJpaRepository : JpaRepository<UserDeckStateEntity, UUID> {
    fun findTop1ByDeckIdOrderByCreatedAtDesc(deckId: UUID): UserDeckStateEntity?
}
