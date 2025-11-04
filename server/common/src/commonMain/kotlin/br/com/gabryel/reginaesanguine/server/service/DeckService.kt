package br.com.gabryel.reginaesanguine.server.service

import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.server.domain.PackDto

/**
 * Deck service for managing card packs.
 * Platform-specific implementations should provide pack loading mechanism.
 */
abstract class DeckService {
    abstract fun loadPack(packId: String): Pack?

    fun getPack(packId: String): PackDto {
        val pack = loadPack(packId)
            ?: throw IllegalArgumentException("Pack $packId not found")

        return PackDto.from(pack)
    }
}
