package br.com.gabryel.reginaesanguine.viewmodel.deck

import br.com.gabryel.reginaesanguine.server.domain.DeckDto
import br.com.gabryel.reginaesanguine.server.domain.DeckPageDto
import br.com.gabryel.reginaesanguine.server.domain.action.CreateDeckRequest
import br.com.gabryel.reginaesanguine.server.domain.action.UpdateDeckRequest

interface DeckClient {
    suspend fun getDecks(): DeckPageDto

    suspend fun createDeck(request: CreateDeckRequest): DeckDto

    suspend fun updateDeck(deckId: String, request: UpdateDeckRequest): DeckDto
}
