package br.com.gabryel.reginaesanguine.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale.Companion.FillWidth
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.services.PainterLoader
import br.com.gabryel.reginaesanguine.app.services.ResourceLoader
import br.com.gabryel.reginaesanguine.app.ui.DeckSelectionScreen
import br.com.gabryel.reginaesanguine.app.ui.GameScreen
import br.com.gabryel.reginaesanguine.app.ui.HomeScreen
import br.com.gabryel.reginaesanguine.app.ui.NavigationStack
import br.com.gabryel.reginaesanguine.app.ui.theme.PurpleDark
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
context(painterLoader: PainterLoader)
fun App(resourceLoader: ResourceLoader) {
    val context = LocalPlatformContext.current

    var loaded by remember { mutableStateOf(false) }
    LaunchedEffect(true) {
        val resources = Res.allDrawableResources.map { (key) ->
            val request = ImageRequest.Builder(context)
                .data(Res.getUri("drawable/$key.png"))
                .build()
            SingletonImageLoader.get(context).enqueue(request)
        }

        resources.forEach { it.job.await() }

        loaded = true
    }

    if (!loaded) {
        Box(Modifier.fillMaxSize(), contentAlignment = Center) {
            CircularProgressIndicator(modifier = Modifier.width(64.dp))
        }
        return
    }

    val packState by produceState<Pack?>(null) {
        value = getStandardPack(resourceLoader)
    }

    val pack = packState ?: return

    val player = Player(emptyList(), pack.cards)
    val gameViewModel = createViewModel(player, player)
    val deckViewModel = DeckViewModel(pack)

    val background = painterLoader.loadStaticImage(Res.drawable.static_temp_fandom_bgblur)

    Box(Modifier.fillMaxSize(), contentAlignment = Center) {
        Image(background, null, Modifier.fillMaxSize(), TopCenter, FillWidth)

        Surface(color = PurpleDark.copy(alpha = 0.6f)) {
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
