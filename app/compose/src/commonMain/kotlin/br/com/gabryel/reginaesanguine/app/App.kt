package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LocalRippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import br.com.gabryel.reginaesanguine.app.Res
import br.com.gabryel.reginaesanguine.app.allDrawableResources
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
import coil3.SingletonImageLoader
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest

@Composable
context(_: CardImageLoader)
fun App(resourceLoader: ResourceLoader) {
    val context = LocalPlatformContext.current
    LaunchedEffect(true) {
        Res.allDrawableResources.forEach { (key) ->
            val request = ImageRequest.Builder(context)
                .data(Res.getUri("drawable/$key.png"))
                .build()
            SingletonImageLoader.get(context).enqueue(request)
        }
    }

    val packState by produceState<Pack?>(null) {
        value = getStandardPack(resourceLoader)
    }

    val pack = packState ?: return

    val player = Player(emptyList(), pack.cards)
    val gameViewModel = createViewModel(player, player)
    val deckViewModel = DeckViewModel(pack)

    Box(Modifier.fillMaxSize().background(PurpleLight), contentAlignment = Center) {
        CompositionLocalProvider(LocalRippleConfiguration provides null) {
            NavigationStack(HOME) {
                addRoute(HOME) {
                    HomeScreen()
                }
                addRoute(DECK_SELECTION) {
                    DeckSelectionScreen(deckViewModel)
                }
                addRoute(GAME) {
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
