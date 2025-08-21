package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.runtime.Composable
import br.com.gabryel.reginaesanguine.app.services.CardImageLoader
import br.com.gabryel.reginaesanguine.app.services.PlayerContext
import br.com.gabryel.reginaesanguine.app.ui.fragments.EditDeck
import br.com.gabryel.reginaesanguine.app.ui.fragments.ViewDecks
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckViewModel

@Composable
context(cardImageLoader: CardImageLoader, nav: NavigationManager<NavigationScreens>)
fun DeckSelectionScreen(deckViewModel: DeckViewModel) {
    context(PlayerContext.left) {
        ViewDecks(deckViewModel)
        EditDeck(deckViewModel)
    }
}
