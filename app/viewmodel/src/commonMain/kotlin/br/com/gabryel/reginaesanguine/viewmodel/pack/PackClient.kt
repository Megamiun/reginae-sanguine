package br.com.gabryel.reginaesanguine.viewmodel.pack

import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.server.domain.page.PackPageDto

/**
 * Client interface for pack operations.
 * Supports both local and remote implementations.
 */
interface PackClient {
    suspend fun getPacks(page: Int = 0, size: Int = 20): PackPageDto

    suspend fun getPackById(packId: String): Pack

    suspend fun getAllPacks(): List<Pack>
}
