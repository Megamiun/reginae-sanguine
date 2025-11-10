package br.com.gabryel.reginaesanguine.server.repository

import br.com.gabryel.reginaesanguine.domain.Pack

interface PackRepository {
    suspend fun savePack(pack: Pack)

    suspend fun packExists(alias: String): Boolean

    suspend fun findPack(alias: String): Pack?

    suspend fun countPacks(): Long

    suspend fun findAllPacks(page: Int, size: Int): List<Pack>
}
