package br.com.gabryel.reginaesanguine.viewmodel.deck

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Pack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DeckViewModel(
    val pack: Pack,
    private val viewFlow: MutableStateFlow<ViewDecks> = MutableStateFlow(ViewDecks(Array(6) { emptyList<Card>() }.toList())),
    private val editFlow: MutableStateFlow<EditDeck?> = MutableStateFlow(null)
) {
    val deckLimit = 15
    val viewDecks = viewFlow.asStateFlow()
    val editDeck = editFlow.asStateFlow()

    fun changeDeckView(index: Int) {
        viewFlow.update { it.copy(selectedDeckIndex = index) }
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

    fun saveDeck() {
        viewFlow.update {
            val editDeck = requireNotNull(editDeck.value) { "No deck being edited, not possible to save deck" }
            viewDecks.value.replaceCurrent(editDeck.deck)
        }

        editFlow.update { null }
    }
}

fun <T> List<T>.replace(index: Int, item: T): List<T> {
    val mutable = toMutableList()
    mutable[index] = item
    return mutable
}
