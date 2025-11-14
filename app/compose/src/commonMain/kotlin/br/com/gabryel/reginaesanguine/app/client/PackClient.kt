package br.com.gabryel.reginaesanguine.app.client

import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.server.client.KtorServerClient
import br.com.gabryel.reginaesanguine.server.client.get
import br.com.gabryel.reginaesanguine.server.dto.PackDto
import br.com.gabryel.reginaesanguine.server.dto.PackPageDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.toList

class PackClient(private val client: KtorServerClient) {
    suspend fun getPacks(page: Int = 0, size: Int = 20): PackPageDto =
        client.get("/api/packs?page=$page&size=$size")

    suspend fun getPackById(packId: String): Pack {
        val packDto: PackDto = client.get("/api/packs/$packId")
        return Pack(
            id = packDto.id,
            name = packDto.name,
            cards = packDto.cards,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getAllPacks(): List<Pack> = flow {
        var page = 0

        while (true) {
            emit(getPacks(page++, 100))
        }
    }
        .takeWhile { it.page != it.totalPages }
        .toList()
        .flatMap { it.content }
        .map { Pack(id = it.id, name = it.name, cards = it.cards) }
}
