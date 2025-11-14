package br.com.gabryel.reginaesanguine.server.service

import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.server.dto.PackDto
import br.com.gabryel.reginaesanguine.server.dto.PackPageDto
import br.com.gabryel.reginaesanguine.server.repository.PackRepository
import kotlin.math.ceil

/**
 * Service for managing card packs and decks.
 * Provides business logic layer between controllers and repository.
 */
class DeckService(private val packRepository: PackRepository) {
    suspend fun loadPack(alias: String): Pack? = packRepository.findPack(alias)

    suspend fun loadPacks(page: Int = 0, size: Int = 10): PackPageDto {
        val totalElements = packRepository.countPacks()
        val packs = packRepository.findAllPacks(page, size)
        val totalPages = ceil(totalElements.toDouble() / size).toInt()

        return PackPageDto(
            content = packs.map { PackDto.from(it) },
            page = page,
            size = size,
            totalElements = totalElements,
            totalPages = totalPages,
        )
    }
}
