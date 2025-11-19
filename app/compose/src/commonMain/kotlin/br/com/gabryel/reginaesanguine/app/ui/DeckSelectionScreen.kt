package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceEvenly
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.services.PainterLoader
import br.com.gabryel.reginaesanguine.app.services.PlayerContext
import br.com.gabryel.reginaesanguine.app.ui.components.NavigationManager
import br.com.gabryel.reginaesanguine.app.ui.components.RButton
import br.com.gabryel.reginaesanguine.app.ui.fragments.deck.EditDeck
import br.com.gabryel.reginaesanguine.app.ui.fragments.deck.ViewDecks
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteLight
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckEditState.DeckEdit
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckEditState.DeckView
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckEditViewModel
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckViewModel
import br.com.gabryel.reginaesanguine.viewmodel.deck.DualDeckEditViewModel

@Composable
context(painterLoader: PainterLoader, nav: NavigationManager<NavigationScreens>)
fun DeckSelectionScreen(deckEditViewModel: DeckEditViewModel) {
    val editState by deckEditViewModel.editState.collectAsState()

    when (val state = editState) {
        is DeckView -> ViewDeck(deckEditViewModel)
        is DeckEdit -> context(PlayerContext.getDefaultFor(state.player)) {
            EditDeck(state.playerViewModel)
        }
    }
}

@Composable
context(painterLoader: PainterLoader, nav: NavigationManager<NavigationScreens>)
private fun ViewDeck(deckEditViewModel: DeckEditViewModel) {
    Column(Modifier.fillMaxSize().padding(15.dp)) {
        RButton("Return", Modifier.align(Start)) { nav.pop() }

        Row(Modifier.fillMaxSize(), SpaceEvenly, CenterVertically) {
            context(PlayerContext.left) {
                PlayerDecksView(deckEditViewModel.leftPlayer)
            }

            if (deckEditViewModel is DualDeckEditViewModel) {
                context(PlayerContext.right) {
                    PlayerDecksView(deckEditViewModel.rightPlayer)
                }
            }
        }
    }
}

@Composable
context(painterLoader: PainterLoader, player: PlayerContext)
private fun PlayerDecksView(deckViewModel: DeckViewModel) {
    val viewDecksState by deckViewModel.viewDecks.collectAsState()

    when {
        viewDecksState.error == "Loading..." -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Center) {
                CircularProgressIndicator(modifier = Modifier.width(64.dp))
            }
        }

        viewDecksState.error != null -> {
            val errorMessage = viewDecksState.error ?: return
            Box(Modifier.fillMaxSize(), contentAlignment = Center) {
                Column(horizontalAlignment = CenterHorizontally) {
                    Text("Error loading decks", color = Color.Red)
                    Text(errorMessage, color = WhiteLight)
                    RButton("Retry") {
                        deckViewModel.refresh()
                    }
                }
            }
        }

        else -> ViewDecks(deckViewModel)
    }
}
