package br.com.gabryel.reginaesanguine.server.entity

import br.com.gabryel.reginaesanguine.server.domain.Account
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "account")
class AccountEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    val username: String,
    val email: String,
    val passwordHash: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain() = Account(
        id = id.toString(),
        username = username,
        email = email,
        passwordHash = passwordHash,
        createdAt = createdAt.toKotlinLocalDateTime(),
        updatedAt = updatedAt.toKotlinLocalDateTime(),
    )

    companion object {
        fun fromDomain(account: Account) = AccountEntity(
            id = null,
            username = account.username,
            email = account.email,
            passwordHash = account.passwordHash,
            createdAt = account.createdAt.toJavaLocalDateTime(),
            updatedAt = account.updatedAt.toJavaLocalDateTime(),
        )
    }
}
