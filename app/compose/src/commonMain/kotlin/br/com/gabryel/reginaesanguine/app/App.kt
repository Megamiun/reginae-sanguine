package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LocalRippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import br.com.gabryel.reginaesanguine.app.services.CardImageLoader
import br.com.gabryel.reginaesanguine.app.services.ResourceLoader
import br.com.gabryel.reginaesanguine.app.ui.theme.PurpleLight
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens.DECK_SELECTION
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens.GAME
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens.HOME
import br.com.gabryel.reginaesanguine.app.util.getStandardPack
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckViewModel
import br.com.gabryel.reginaesanguine.viewmodel.game.GameViewModel

@Composable
context(_: CardImageLoader)
fun App(resourceLoader: ResourceLoader) {
    val packState by produceState<Pack?>(null) {
        value = getStandardPack(resourceLoader)
    }

    val pack = packState ?: return

    val player = Player(emptyList(), pack.cards)
    val gameViewModel = createViewModel(player, player)
    val deckViewModel = DeckViewModel(pack)

    CompositionLocalProvider(LocalRippleConfiguration provides null) {
        NavigationStack(DECK_SELECTION) {
            addRoute(HOME) {
                HomeScreen()
            }
            addRoute(DECK_SELECTION) {
                Box(Modifier.fillMaxSize().background(PurpleLight), contentAlignment = Center) {
                    DeckSelectionScreen(deckViewModel)
                }
            }
            addRoute(GAME) {
                Box(Modifier.fillMaxSize().background(PurpleLight), contentAlignment = Center) {
                    GameScreen(gameViewModel)
                }
            }
        }
    }
}

private fun createViewModel(left: Player, right: Player): GameViewModel {
    val game = Game.forPlayers(left, right)
    return GameViewModel.forGame(game)
}
