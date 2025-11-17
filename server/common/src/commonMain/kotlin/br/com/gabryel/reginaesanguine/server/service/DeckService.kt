package br.com.gabryel.reginaesanguine.server.service

import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.server.domain.PackDto
import br.com.gabryel.reginaesanguine.server.domain.PackPageDto
import br.com.gabryel.reginaesanguine.server.repository.PackRepository

/**
 * Service for managing card packs and decks.
 * Provides business logic layer between controllers and repository.
 */
class DeckService(private val packRepository: PackRepository) {
    suspend fun loadPack(alias: String): Pack? = packRepository.findPack(alias)

    suspend fun loadPacks(page: Int = 0, size: Int = 10): PackPageDto {
        val packs = packRepository.findAllPacks(page, size)

        return PackPageDto(
            content = packs.content.map { PackDto.from(it) },
            page = page,
            size = size,
            totalElements = packs.totalElements,
            totalPages = packs.totalPages,
        )
    }
}
