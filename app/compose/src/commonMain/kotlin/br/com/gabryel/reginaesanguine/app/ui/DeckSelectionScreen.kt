package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceEvenly
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.services.PainterLoader
import br.com.gabryel.reginaesanguine.app.services.PlayerContext
import br.com.gabryel.reginaesanguine.app.ui.components.NavigationManager
import br.com.gabryel.reginaesanguine.app.ui.components.RButton
import br.com.gabryel.reginaesanguine.app.ui.fragments.deck.EditDeck
import br.com.gabryel.reginaesanguine.app.ui.fragments.deck.ViewDecks
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckEditState.DeckEdit
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckEditState.DeckView
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckEditViewModel
import br.com.gabryel.reginaesanguine.viewmodel.deck.LocalDeckViewModel

@Composable
context(painterLoader: PainterLoader, nav: NavigationManager<NavigationScreens>)
fun DeckSelectionScreen(deckEditViewModel: DeckEditViewModel) {
    val editState by deckEditViewModel.editState.collectAsState()

    when (val state = editState) {
        is DeckView -> View(deckEditViewModel)
        is DeckEdit ->
            context(PlayerContext.getDefaultFor(state.player)) {
                EditDeck(state.playerViewModel)
            }
    }
}

@Composable
context(painterLoader: PainterLoader, nav: NavigationManager<NavigationScreens>)
private fun View(deckEditViewModel: DeckEditViewModel) {
    Column(Modifier.fillMaxSize().padding(15.dp)) {
        RButton("Return", Modifier.align(Start)) { nav.pop() }

        Row(Modifier.fillMaxSize(), SpaceEvenly, CenterVertically) {
            context(PlayerContext.left) {
                ViewDecks(deckEditViewModel.leftPlayer)
            }
            if (deckEditViewModel is LocalDeckViewModel) context(PlayerContext.right) {
                ViewDecks(deckEditViewModel.rightPlayer)
            }
        }
    }
}
