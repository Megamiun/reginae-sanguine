package br.com.gabryel.reginaesanguine.viewmodel.deck

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Pack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.collections.emptyList

class DeckViewModel(
    val pack: Pack,
    private val viewFlow: MutableStateFlow<ViewDecks> = MutableStateFlow(ViewDecks(Array(6) { emptyList<Card>() }.toList())),
    private val editFlow: MutableStateFlow<EditDeck?> = MutableStateFlow(null)
) {
    val viewDecks = viewFlow.asStateFlow()
    val editDeck = editFlow.asStateFlow()

    fun changeDeckView(index: Int) {
        viewFlow.update { it.copy(selectedDeckIndex = index) }
    }

    fun enterEditMode() {
        editFlow.update { EditDeck(viewDecks.value.selectedDeck) }
    }

    fun addToDeck(card: Card) {
        editFlow.update {
            requireNotNull(it)
            it.addToDeck(card)
        }
    }
}
