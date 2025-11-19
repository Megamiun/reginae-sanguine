package br.com.gabryel.reginaesanguine.server.node.repository

import br.com.gabryel.reginaesanguine.server.domain.GameRequestDto
import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatus
import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatus.STARTED
import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatus.STARTING
import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatus.WAITING
import br.com.gabryel.reginaesanguine.server.domain.page.GenericPageDto
import br.com.gabryel.reginaesanguine.server.domain.page.PageDto
import br.com.gabryel.reginaesanguine.server.node.pg.Pool
import br.com.gabryel.reginaesanguine.server.repository.GameRequestRepository
import kotlinx.coroutines.await
import kotlin.math.ceil

class NodeGameRequestRepository(private val pool: Pool) : GameRequestRepository {

    override suspend fun create(creatorAccountId: String, creatorDeckStateId: String): GameRequestDto {
        val result = pool.query(
            """
            INSERT INTO game_request (id, creator_account_id, creator_deck_state_id, status)
            VALUES ($1::uuid, $2::uuid, $3::uuid, $4)
            RETURNING id, creator_account_id, creator_deck_state_id, status, game_id, joiner_account_id, joiner_deck_state_id
            """.trimIndent(),
            arrayOf(generateUUID(), creatorAccountId, creatorDeckStateId, WAITING.name),
        ).await()

        return toDto(result.rows[0])
    }

    override suspend fun join(id: String, joinerAccountId: String, joinerDeckStateId: String): GameRequestDto {
        val result = pool.query(
            """
            UPDATE game_request
            SET joiner_account_id = $2::uuid, joiner_deck_state_id = $3::uuid, status = $4
            WHERE id = $1::uuid
            RETURNING id, creator_account_id, creator_deck_state_id, status, game_id, joiner_account_id, joiner_deck_state_id
            """.trimIndent(),
            arrayOf(id, joinerAccountId, joinerDeckStateId, STARTING.name),
        ).await()

        return toDto(result.rows[0])
    }

    override suspend fun associateGame(id: String, gameId: String): GameRequestDto {
        val result = pool.query(
            """
            UPDATE game_request
            SET game_id = $2::uuid, status = $3
            WHERE id = $1::uuid
            RETURNING id, creator_account_id, creator_deck_state_id, status, game_id, joiner_account_id, joiner_deck_state_id
            """.trimIndent(),
            arrayOf(id, gameId, STARTED.name),
        ).await()

        return toDto(result.rows[0])
    }

    override suspend fun findById(id: String): GameRequestDto? {
        val result = pool.query(
            """
                SELECT id, creator_account_id, creator_deck_state_id, status, game_id, joiner_account_id, joiner_deck_state_id FROM game_request 
                WHERE id = $1::uuid
                """, arrayOf(id)
        ).await()

        if (result.rows.isEmpty()) return null

        return toDto(result.rows[0])
    }

    override suspend fun findAllByStatus(
        status: GameRequestStatus,
        page: Int,
        size: Int
    ): PageDto<GameRequestDto> {
        val offset = page * size
        val totalElements = countByStatus(status)

        val packsResult = pool.query(
            """
            SELECT id, creator_account_id, creator_deck_state_id, status, game_id, joiner_account_id, joiner_deck_state_id FROM game_request
            WHERE status = $3
            LIMIT $1 OFFSET $2
            """, arrayOf(size, offset, status.name)
        ).await()

        val totalPages = if (size > 0) {
            ceil(totalElements.toDouble() / size.toDouble()).toInt()
        } else {
            0
        }

        return GenericPageDto(
            packsResult.rows.mapNotNull(::toDto),
            page,
            size,
            totalElements,
            totalPages,
        )
    }

    private fun toDto(row: dynamic) = GameRequestDto(
        row.id as String,
        row.creator_account_id as String,
        row.creator_deck_state_id as String,
        GameRequestStatus.valueOf(row.status as String),
        row.game_id as String?,
        row.joiner_account_id as String?,
        row.joiner_deck_state_id as String?
    )

    private suspend fun countByStatus(status: GameRequestStatus): Long {
        val result = pool.query(
            "SELECT COUNT(*) as count FROM game_request WHERE status = $1",
            arrayOf(status.name),
        ).await()

        if (result.rows.isEmpty()) return 0

        return when (val countValue = result.rows[0].count) {
            is Number -> countValue.toLong()
            is String -> js("parseInt(countValue)").unsafeCast<Int>().toLong()
            else -> 0
        }
    }
}
