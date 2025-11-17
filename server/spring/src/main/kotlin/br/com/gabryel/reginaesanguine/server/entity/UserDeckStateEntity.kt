package br.com.gabryel.reginaesanguine.server.entity

import br.com.gabryel.reginaesanguine.server.domain.AccountDeck
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "account_deck_state")
class UserDeckStateEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    val deckId: UUID,
    @JdbcTypeCode(SqlTypes.ARRAY)
    val cardIds: List<String>,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain(deckEntity: UserDeckEntity, packAlias: String) = AccountDeck(
        stateId = id.toString(),
        packId = packAlias,
        accountId = deckEntity.accountId.toString(),
        id = deckId.toString(),
        cardIds = cardIds,
    )
}
