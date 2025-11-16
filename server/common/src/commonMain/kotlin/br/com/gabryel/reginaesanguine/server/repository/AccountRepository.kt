package br.com.gabryel.reginaesanguine.server.repository

import br.com.gabryel.reginaesanguine.server.domain.Account

interface AccountRepository {
    suspend fun save(account: Account): Account

    suspend fun findByUsername(username: String): Account?

    suspend fun existsByUsername(username: String): Boolean

    suspend fun existsByEmail(email: String): Boolean
}
