package br.com.gabryel.reginaesanguine.viewmodel.pack.remote

import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.server.client.ServerClient
import br.com.gabryel.reginaesanguine.server.client.get
import br.com.gabryel.reginaesanguine.server.domain.PackDto
import br.com.gabryel.reginaesanguine.server.domain.page.PackPageDto
import br.com.gabryel.reginaesanguine.viewmodel.pack.PackClient
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.toList

/**
 * Remote implementation of PackClient that communicates with a server via HTTP.
 */
class RemotePackClient(private val client: ServerClient) : PackClient {
    override suspend fun getPacks(page: Int, size: Int): PackPageDto =
        client.get("/pack?page=$page&size=$size")

    override suspend fun getPackById(packId: String): Pack {
        val packDto = client.get<PackDto>("/pack/$packId")
        return Pack(
            id = packDto.id,
            name = packDto.name,
            cards = packDto.cards,
        )
    }

    override suspend fun getAllPacks(): List<Pack> = flow {
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
