package br.com.gabryel.reginaesanguine.server.node.repository

import br.com.gabryel.reginaesanguine.server.domain.AccountDeck
import br.com.gabryel.reginaesanguine.server.domain.PageDto
import br.com.gabryel.reginaesanguine.server.domain.map
import br.com.gabryel.reginaesanguine.server.node.pg.Pool
import br.com.gabryel.reginaesanguine.server.repository.AccountDeckRepository
import kotlinx.coroutines.await
import kotlinx.datetime.Clock

class NodeAccountDeckRepository(private val pool: Pool) : AccountDeckRepository {
    override suspend fun create(accountId: String, packAlias: String, cardIds: List<String>): AccountDeck {
        val packId = findPackIdByAlias(packAlias)
            ?: error("Pack '$packAlias' does not exist.")

        val deckResult = pool.query(
            """
            INSERT INTO account_deck (id, account_id, pack_id)
            VALUES ($1::uuid, $2::uuid, $3::uuid)
            RETURNING id, account_id, pack_id
            """.trimIndent(),
            arrayOf(generateUUID(), accountId, packId),
        ).await()

        val deckRow = deckResult.rows[0]
        return saveState(deckRow, packAlias, cardIds)
    }

    override suspend fun update(deckId: String, cardIds: List<String>): AccountDeck {
        val deckRow = findDeckRow(deckId) ?: error("Deck '$deckId' does not exist.")
        val packAlias = findPackAliasByPackId(deckRow.pack_id)
            ?: error("Pack with id ${deckRow.pack_id} does not exist.")
        return saveState(deckRow, packAlias, cardIds)
    }

    override suspend fun findById(id: String): AccountDeck? {
        val deckRow = findDeckRow(id) ?: return null
        val stateRow = findLatestDeckStateRow(id) ?: return null
        val packAlias = findPackAliasByPackId(deckRow.pack_id) ?: return null
        return rowToUserDeck(deckRow, stateRow, packAlias)
    }

    override suspend fun findByStateId(stateId: String): AccountDeck? {
        val stateRow = findDeckStateRow(stateId) ?: return null
        val deckRow = findDeckRow(stateRow.deck_id) ?: return null
        val packAlias = findPackAliasByPackId(deckRow.pack_id) ?: return null
        return rowToUserDeck(deckRow, stateRow, packAlias)
    }

    override suspend fun findByAccountId(accountId: String): PageDto<AccountDeck> {
        val result = pool.query(
            """
            SELECT id, account_id, pack_id FROM account_deck
            WHERE account_id = $1::uuid
            """.trimIndent(),
            arrayOf(accountId),
        ).await()

        return PageDto.singlePage(result.rows.toList()).map { deckRow ->
            val stateRow = findLatestDeckStateRow(deckRow.id)
                ?: error("Deck ${deckRow.id} has no state")
            val packAlias = findPackAliasByPackId(deckRow.pack_id)
                ?: error("Pack with id ${deckRow.pack_id} does not exist.")
            rowToUserDeck(deckRow, stateRow, packAlias)
        }
    }

    override suspend fun countByAccountId(accountId: String): Int {
        val result = pool.query(
            "SELECT COUNT(*) as count FROM account_deck WHERE account_id = $1::uuid",
            arrayOf(accountId),
        ).await()

        if (result.rows.isEmpty()) return 0

        return when (val countValue = result.rows[0].count) {
            is Number -> countValue.toInt()
            is String -> js("parseInt(countValue)").unsafeCast<Int>()
            else -> 0
        }
    }

    override suspend fun existsByIdAndAccountId(id: String, accountId: String): Boolean {
        val result = pool.query(
            "SELECT COUNT(*) as count FROM account_deck WHERE id = $1::uuid AND account_id = $2::uuid",
            arrayOf(id, accountId),
        ).await()

        if (result.rows.isEmpty()) return false

        return when (val countValue = result.rows[0].count) {
            is Number -> countValue.toInt()
            is String -> js("parseInt(countValue)").unsafeCast<Int>()
            else -> 0
        } > 0
    }

    private suspend fun saveState(deckRow: dynamic, packAlias: String, cardIds: List<String>): AccountDeck {
        val stateResult = pool.query(
            """
            INSERT INTO account_deck_state (id, deck_id, card_ids, created_at)
            VALUES ($1::uuid, $2::uuid, $3::text[], $4::timestamp)
            RETURNING id, card_ids
            """.trimIndent(),
            arrayOf(
                generateUUID(),
                deckRow.id,
                cardIds.toTypedArray(),
                Clock.System.now().toString(),
            ),
        ).await()

        val stateRow = stateResult.rows[0]
        return rowToUserDeck(deckRow, stateRow, packAlias)
    }

    private fun rowToUserDeck(deckRow: dynamic, stateRow: dynamic, packAlias: String) = AccountDeck(
        id = deckRow.id,
        accountId = deckRow.account_id,
        packId = packAlias,
        stateId = stateRow.id,
        cardIds = stateRow.card_ids.unsafeCast<Array<String>>().toList(),
    )

    private suspend fun findPackIdByAlias(alias: String): String? {
        val result = pool.query(
            "SELECT id FROM pack WHERE alias = $1",
            arrayOf(alias),
        ).await()
        return if (result.rows.isEmpty()) null else result.rows[0].id
    }

    private suspend fun findPackAliasByPackId(packId: String): String? {
        val result = pool.query(
            "SELECT alias FROM pack WHERE id = $1::uuid",
            arrayOf(packId),
        ).await()
        return if (result.rows.isEmpty()) null else result.rows[0].alias
    }

    private suspend fun findDeckRow(id: String): dynamic? {
        val result = pool.query(
            """
            SELECT id, account_id, pack_id FROM account_deck
            WHERE id = $1::uuid
            """.trimIndent(),
            arrayOf(id),
        ).await()
        return if (result.rows.isEmpty()) null else result.rows[0]
    }

    private suspend fun findLatestDeckStateRow(deckId: String): dynamic? {
        val result = pool.query(
            """
            SELECT id, deck_id, card_ids FROM account_deck_state
            WHERE deck_id = $1::uuid
            ORDER BY created_at DESC
            LIMIT 1
            """.trimIndent(),
            arrayOf(deckId),
        ).await()
        return if (result.rows.isEmpty()) null else result.rows[0]
    }

    private suspend fun findDeckStateRow(stateId: String): dynamic? {
        val result = pool.query(
            """
            SELECT id, deck_id, card_ids FROM account_deck_state
            WHERE id = $1::uuid
            """.trimIndent(),
            arrayOf(stateId),
        ).await()
        return if (result.rows.isEmpty()) null else result.rows[0]
    }
}
