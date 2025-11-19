package br.com.gabryel.reginaesanguine.server.service

import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.server.domain.PackDto
import br.com.gabryel.reginaesanguine.server.domain.page.PackPageDto
import br.com.gabryel.reginaesanguine.server.domain.page.map
import br.com.gabryel.reginaesanguine.server.repository.PackRepository

/**
 * Service for managing card packs and decks.
 * Provides business logic layer between controllers and repository.
 */
class DeckService(private val packRepository: PackRepository) {
    suspend fun loadPack(alias: String): Pack? = packRepository.findPack(alias)

    suspend fun loadPacks(page: Int = 0, size: Int = 10): PackPageDto {
        return packRepository.findAllPacks(page, size)
            .map { PackDto.from(it) }
            .upcast(::PackPageDto)
    }
}
