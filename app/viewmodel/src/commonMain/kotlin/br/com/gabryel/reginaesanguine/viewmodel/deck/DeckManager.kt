package br.com.gabryel.reginaesanguine.viewmodel.deck

import br.com.gabryel.reginaesanguine.domain.Card
import kotlinx.coroutines.flow.StateFlow

sealed interface DeckManagerState {
    data object Loading : DeckManagerState

    data class Loaded(val decks: List<List<Card>>, val selectedIndex: Int = 0) : DeckManagerState {
        val selectedDeck: List<Card> = decks.getOrElse(selectedIndex) { emptyList() }
        val deckCount: Int = decks.size
    }

    data class Error(val message: String) : DeckManagerState
}

interface DeckManager {
    val state: StateFlow<DeckManagerState>

    fun selectDeck(index: Int)

    suspend fun saveDeck(deck: List<Card>)

    suspend fun createDeck(deck: List<Card>)

    fun refresh()
}
