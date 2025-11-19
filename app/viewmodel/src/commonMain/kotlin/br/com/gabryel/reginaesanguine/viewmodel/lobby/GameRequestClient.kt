package br.com.gabryel.reginaesanguine.viewmodel.lobby

import br.com.gabryel.reginaesanguine.server.domain.GameRequestDto
import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatusDto
import br.com.gabryel.reginaesanguine.server.domain.page.GameRequestPageDto

interface GameRequestClient {
    suspend fun create(deckStateId: String): GameRequestDto

    suspend fun listAvailable(): GameRequestPageDto

    suspend fun join(gameRequestId: String, deckStateId: String): GameRequestStatusDto

    suspend fun getStatus(gameRequestId: String): GameRequestStatusDto?
}
