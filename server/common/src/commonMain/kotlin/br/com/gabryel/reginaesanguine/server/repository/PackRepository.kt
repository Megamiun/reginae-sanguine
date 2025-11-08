package br.com.gabryel.reginaesanguine.server.repository

import br.com.gabryel.reginaesanguine.domain.Pack

interface PackRepository {
    suspend fun savePack(pack: Pack)

    suspend fun packExists(packId: String): Boolean

    suspend fun findPack(packId: String): Pack?

    suspend fun findAllPacks(): List<Pack>
}
