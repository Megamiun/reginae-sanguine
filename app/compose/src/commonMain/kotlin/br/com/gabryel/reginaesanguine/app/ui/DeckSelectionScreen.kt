package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.gabryel.reginaesanguine.app.services.PainterLoader
import br.com.gabryel.reginaesanguine.app.services.PlayerContext
import br.com.gabryel.reginaesanguine.app.ui.components.NavigationManager
import br.com.gabryel.reginaesanguine.app.ui.fragments.EditDeck
import br.com.gabryel.reginaesanguine.app.ui.fragments.ViewDecks
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckViewModel

@Composable
context(painterLoader: PainterLoader, nav: NavigationManager<NavigationScreens>)
fun DeckSelectionScreen(deckViewModel: DeckViewModel) {
    Box(Modifier.fillMaxSize()) {
        context(PlayerContext.left) {
            ViewDecks(deckViewModel)
            EditDeck(deckViewModel)
        }
    }
}
