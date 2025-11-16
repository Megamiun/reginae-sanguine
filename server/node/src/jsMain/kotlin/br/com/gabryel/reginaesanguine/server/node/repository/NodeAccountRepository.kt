package br.com.gabryel.reginaesanguine.server.node.repository

import br.com.gabryel.reginaesanguine.server.domain.Account
import br.com.gabryel.reginaesanguine.server.node.pg.Pool
import br.com.gabryel.reginaesanguine.server.repository.AccountRepository
import kotlinx.coroutines.await
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime

class NodeAccountRepository(private val pool: Pool) : AccountRepository {
    override suspend fun save(account: Account): Account {
        val result = pool.query(
            """
            INSERT INTO account (id, username, email, password_hash, created_at, updated_at)
            VALUES ($1, $2, $3, $4, $5::timestamp, $6::timestamp)
            RETURNING id, username, email, password_hash, created_at, updated_at
            """.trimIndent(),
            arrayOf(
                generateUUID(),
                account.username,
                account.email,
                account.passwordHash,
                account.createdAt.toString(),
                account.updatedAt.toString(),
            ),
        ).await()

        val row = result.rows[0]
        return Account(
            id = row.id,
            username = row.username,
            email = row.email,
            passwordHash = row.password_hash,
            createdAt = parseTimestamp(row.created_at),
            updatedAt = parseTimestamp(row.updated_at),
        )
    }

    override suspend fun findByUsername(username: String): Account? {
        val result = pool.query(
            "SELECT id, username, email, password_hash, created_at, updated_at FROM account WHERE username = $1",
            arrayOf(username),
        ).await()

        if (result.rows.isEmpty()) return null

        val row = result.rows[0]
        return Account(
            id = row.id,
            username = row.username,
            email = row.email,
            passwordHash = row.password_hash,
            createdAt = parseTimestamp(row.created_at),
            updatedAt = parseTimestamp(row.updated_at),
        )
    }

    override suspend fun existsByUsername(username: String): Boolean {
        val result = pool.query(
            "SELECT COUNT(*) as count FROM account WHERE username = $1",
            arrayOf(username),
        ).await()

        if (result.rows.isEmpty()) return false

        val count = when (val countValue = result.rows[0].count) {
            is Number -> countValue.toInt()
            is String -> js("parseInt(countValue)").unsafeCast<Int>()
            else -> 0
        }

        return count > 0
    }

    override suspend fun existsByEmail(email: String): Boolean {
        val result = pool.query(
            "SELECT COUNT(*) as count FROM account WHERE email = $1",
            arrayOf(email),
        ).await()

        if (result.rows.isEmpty()) return false

        val count = when (val countValue = result.rows[0].count) {
            is Number -> countValue.toInt()
            is String -> js("parseInt(countValue)").unsafeCast<Int>()
            else -> 0
        }

        return count > 0
    }

    private fun parseTimestamp(value: dynamic): LocalDateTime {
        val millis = js("value.getTime()").unsafeCast<Double>().toLong()
        return Instant.fromEpochMilliseconds(millis).toLocalDateTime(UTC)
    }
}
