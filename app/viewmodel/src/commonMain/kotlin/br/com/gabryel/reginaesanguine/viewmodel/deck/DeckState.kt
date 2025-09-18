package br.com.gabryel.reginaesanguine.viewmodel.deck

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.CardTier.LEGENDARY
import br.com.gabryel.reginaesanguine.domain.CardTier.STANDARD

data class ViewDecks(val decks: List<List<Card>>, val selectedDeckIndex: Int = 0, val error: String? = null) {
    val deckAmount: Int = decks.size
    val selectedDeck = decks[selectedDeckIndex]

    fun replaceCurrent(deck: List<Card>) = copy(decks = decks.replace(selectedDeckIndex, deck))
}

data class EditDeck(val deck: List<Card>, val deckLimit: Int = 15, val error: String? = null) {
    fun addToDeck(card: Card) =
        if (getAvailable(card) == 0) this
        else copy(deck = deck + card)

    fun removeFromDeck(card: Card) =
        copy(deck = deck - card)

    fun getAvailable(card: Card): Int = getMax(card) - deck.count { it.id == card.id }

    fun getMax(card: Card): Int = when (card.tier) {
        STANDARD -> 2
        LEGENDARY -> 1
    }
}
