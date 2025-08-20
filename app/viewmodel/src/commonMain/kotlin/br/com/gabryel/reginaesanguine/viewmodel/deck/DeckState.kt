package br.com.gabryel.reginaesanguine.viewmodel.deck

import br.com.gabryel.reginaesanguine.domain.Card

data class ViewDecks(val decks: List<List<Card>>, val selectedDeckIndex: Int = 0, val error: String? = null) {
    val deckAmount: Int = decks.size
    val selectedDeck = decks[selectedDeckIndex]
}

data class EditDeck(val deck: List<Card>, val error: String? = null) {
    val deckLimit = 15

    fun addToDeck(card: Card) = copy(deck = deck + card)
}
