package br.com.gabryel.reginaesanguine.viewmodel.deck.remote

import br.com.gabryel.reginaesanguine.server.client.ServerClient
import br.com.gabryel.reginaesanguine.server.client.get
import br.com.gabryel.reginaesanguine.server.client.post
import br.com.gabryel.reginaesanguine.server.client.put
import br.com.gabryel.reginaesanguine.server.domain.DeckDto
import br.com.gabryel.reginaesanguine.server.domain.action.CreateDeckRequest
import br.com.gabryel.reginaesanguine.server.domain.action.UpdateDeckRequest
import br.com.gabryel.reginaesanguine.server.domain.page.DeckPageDto
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckClient

class RemoteDeckClient(private val client: ServerClient, token: String) : DeckClient {
    private val authHeaders = mapOf("Authorization" to "Bearer $token")

    override suspend fun getDecks(): DeckPageDto =
        client.get("account-deck", authHeaders)

    override suspend fun createDeck(request: CreateDeckRequest): DeckDto =
        client.post("account-deck", request, authHeaders)

    override suspend fun updateDeck(deckId: String, request: UpdateDeckRequest): DeckDto =
        client.put("account-deck/$deckId", request, authHeaders)
}
