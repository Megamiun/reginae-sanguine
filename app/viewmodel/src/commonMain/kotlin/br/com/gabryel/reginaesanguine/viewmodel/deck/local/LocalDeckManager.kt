package br.com.gabryel.reginaesanguine.viewmodel.deck.local

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckManager
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckManagerState
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckManagerState.Loaded
import br.com.gabryel.reginaesanguine.viewmodel.deck.replace
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LocalDeckManager(pack: Pack) : DeckManager {
    private val stateFlow = MutableStateFlow<DeckManagerState>(
        Loaded(
            decks = Array(6) { pack.cards.filter { !it.spawnOnly }.take(15) }.toList(),
        ),
    )

    override val state = stateFlow.asStateFlow()

    override fun selectDeck(index: Int) {
        stateFlow.update { current ->
            when (current) {
                is Loaded -> current.copy(selectedIndex = index)
                else -> current
            }
        }
    }

    override suspend fun saveDeck(deck: List<Card>) {
        stateFlow.update { current ->
            when (current) {
                is Loaded -> current.copy(decks = current.decks.replace(current.selectedIndex, deck))
                else -> current
            }
        }
    }

    override suspend fun createDeck(deck: List<Card>) {
        stateFlow.update { current ->
            when (current) {
                is Loaded -> current.copy(decks = current.decks + listOf(deck), selectedIndex = current.decks.size)
                else -> current
            }
        }
    }

    override fun refresh() {
    }
}
