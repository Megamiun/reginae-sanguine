package br.com.gabryel.reginaesanguine.server.entity

import br.com.gabryel.reginaesanguine.server.domain.GameRequestDto
import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcType
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "game_request")
class GameRequestEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    val creatorAccountId: UUID,
    val creatorDeckStateId: UUID,
    @Enumerated(STRING)
    @JdbcType(value = PostgreSQLEnumJdbcType::class)
    var status: GameRequestStatus,
    var gameId: UUID? = null,
    var joinerAccountId: UUID? = null,
    var joinerDeckStateId: UUID? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDto() = GameRequestDto(
        id = id.toString(),
        creatorAccountId = creatorAccountId.toString(),
        creatorDeckStateId = creatorDeckStateId.toString(),
        status = status,
        gameId = gameId?.toString(),
        joinerAccountId = joinerAccountId?.toString(),
        joinerDeckStateId = joinerDeckStateId?.toString(),
    )
}
