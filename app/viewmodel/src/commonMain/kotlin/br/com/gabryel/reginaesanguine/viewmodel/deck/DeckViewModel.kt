package br.com.gabryel.reginaesanguine.viewmodel.deck

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Pack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeckViewModel(
    val pack: Pack,
    private val deckManager: DeckManager,
    private val coroutineScope: CoroutineScope
) {
    private val editFlow = MutableStateFlow<EditDeck?>(null)
    val editDeck = editFlow.asStateFlow()

    val viewDecks: StateFlow<ViewDecks> = deckManager.state.map { managerState ->
        when (managerState) {
            is DeckManagerState.Loading -> ViewDecks(listOf(emptyList()), error = "Loading...")
            is DeckManagerState.Error -> ViewDecks(listOf(emptyList()), error = managerState.message)
            is DeckManagerState.Loaded -> ViewDecks(managerState.decks, managerState.selectedIndex)
        }
    }.stateIn(coroutineScope, Eagerly, ViewDecks(listOf(emptyList())))

    fun changeDeckView(index: Int) {
        deckManager.selectDeck(index)
    }

    fun enterEditMode() {
        editFlow.update { EditDeck(viewDecks.value.selectedDeck) }
    }

    fun cancelEditMode() {
        editFlow.update { null }
    }

    fun addToDeck(card: Card) {
        editFlow.update {
            requireNotNull(it) { "Not on edit flow, should not be able to add cards" }
            it.addToDeck(card)
        }
    }

    fun removeFromDeck(card: Card) {
        editFlow.update {
            requireNotNull(it) { "Not on edit flow, should not be able to remove cards" }
            it.removeFromDeck(card)
        }
    }

    fun saveDeck() {
        val currentEdit = editDeck.value ?: return
        coroutineScope.launch {
            deckManager.saveDeck(currentEdit.deck)
            editFlow.update { null }
        }
    }

    fun createDeck() {
        coroutineScope.launch {
            val initialDeck = pack.cards.filter { !it.spawnOnly }.take(15)
            deckManager.createDeck(initialDeck)
        }
    }

    fun refresh() {
        deckManager.refresh()
    }
}
