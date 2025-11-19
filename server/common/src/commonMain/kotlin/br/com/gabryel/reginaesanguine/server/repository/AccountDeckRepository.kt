package br.com.gabryel.reginaesanguine.server.repository

import br.com.gabryel.reginaesanguine.server.domain.AccountDeck
import br.com.gabryel.reginaesanguine.server.domain.page.PageDto

interface AccountDeckRepository {
    suspend fun create(accountId: String, packAlias: String, cardIds: List<String>): AccountDeck

    suspend fun update(deckId: String, cardIds: List<String>): AccountDeck

    suspend fun findById(id: String): AccountDeck?

    suspend fun findByStateId(stateId: String): AccountDeck?

    suspend fun findByAccountId(accountId: String): PageDto<AccountDeck>

    suspend fun countByAccountId(accountId: String): Int

    suspend fun existsByIdAndAccountId(id: String, accountId: String): Boolean
}
