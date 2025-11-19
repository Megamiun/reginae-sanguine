package br.com.gabryel.reginaesanguine.viewmodel.deck.remote

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.server.domain.DeckDto
import br.com.gabryel.reginaesanguine.server.domain.action.CreateDeckRequest
import br.com.gabryel.reginaesanguine.server.domain.action.UpdateDeckRequest
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckClient
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckManager
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckManagerState
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckManagerState.Error
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckManagerState.Loaded
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckManagerState.Loading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RemoteDeckManager(
    private val deckClient: DeckClient,
    private val coroutineScope: CoroutineScope,
    private val pack: Pack
) : DeckManager {
    private val stateFlow = MutableStateFlow<DeckManagerState>(Loading)
    override val state = stateFlow.asStateFlow()

    private var serverDecks: List<DeckDto> = emptyList()

    override fun selectDeck(index: Int) {
        stateFlow.update { current ->
            when (current) {
                is Loaded -> current.copy(selectedIndex = index)
                else -> current
            }
        }
    }

    override suspend fun createDeck(deck: List<Card>) {
        try {
            val request = CreateDeckRequest(pack.id, deck.map { it.id })
            val newDeck = deckClient.createDeck(request)

            serverDecks = serverDecks + newDeck

            stateFlow.update {
                when (it) {
                    is Loaded -> it.copy(
                        decks = serverDecks.map { dto -> mapDeckDtoToCards(dto) },
                        selectedIndex = serverDecks.size - 1,
                    )

                    else -> it
                }
            }
        } catch (e: Exception) {
            stateFlow.value = Error(e.message ?: "Failed to create deck")
        }
    }

    override suspend fun saveDeck(deck: List<Card>) {
        val current = state.value as? Loaded ?: return
        val serverDeck = serverDecks.getOrNull(current.selectedIndex) ?: return

        try {
            val request = UpdateDeckRequest(deck.map { it.id })
            val updatedDeck = deckClient.updateDeck(serverDeck.id, request)

            serverDecks = serverDecks.mapIndexed { index, dto ->
                if (index == current.selectedIndex) updatedDeck else dto
            }

            stateFlow.update {
                when (it) {
                    is Loaded -> it.copy(decks = serverDecks.map { dto -> mapDeckDtoToCards(dto) })
                    else -> it
                }
            }
        } catch (e: Exception) {
            stateFlow.value = Error(e.message ?: "Failed to save deck")
        }
    }

    override fun refresh() {
        coroutineScope.launch {
            stateFlow.value = Loading
            try {
                serverDecks = deckClient.getDecks().content
                val decks = serverDecks.map { mapDeckDtoToCards(it) }
                stateFlow.value = Loaded(decks)
            } catch (e: Exception) {
                stateFlow.value = Error(e.message ?: "Failed to load decks")
            }
        }
    }

    fun getSelectedDeckDto(): DeckDto? {
        val current = state.value as? Loaded ?: return null
        return serverDecks.getOrNull(current.selectedIndex)
    }

    private fun mapDeckDtoToCards(deckDto: DeckDto): List<Card> =
        deckDto.cardIds.mapNotNull { cardId -> pack.cards.find { it.id == cardId } }
}
