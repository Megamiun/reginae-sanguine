package br.com.gabryel.reginaesanguine.server.service

import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.server.repository.PackRepository

/**
 * Service for managing card packs and decks.
 * Provides business logic layer between controllers and repository.
 */
class DeckService(private val packRepository: PackRepository) {
    suspend fun loadPack(packId: String): Pack? = packRepository.findPack(packId)
}
