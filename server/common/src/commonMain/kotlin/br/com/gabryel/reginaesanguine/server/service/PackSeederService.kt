package br.com.gabryel.reginaesanguine.server.service

import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.server.repository.PackRepository
import kotlinx.serialization.Serializable

class PackSeederService(
    private val packRepository: PackRepository,
    private val packLoader: PackLoader
) {
    suspend fun seedPacks(): SeedResult {
        val availablePacks = packLoader.loadAllPacks()
        val (seededPacks, skippedPacks) = availablePacks.partition { pack ->
            val exists = packRepository.packExists(pack.id)

            if (!exists) packRepository.savePack(pack)
            !exists
        }

        return SeedResult(seededPacks.map { it.id }, skippedPacks.map { it.id })
    }
}

@Serializable
data class SeedResult(
    val seeded: List<String>,
    val skipped: List<String>
)

interface PackLoader {
    fun loadAllPacks(): List<Pack>
}
