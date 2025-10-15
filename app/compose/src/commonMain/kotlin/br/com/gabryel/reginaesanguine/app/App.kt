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
import androidx.compose.runtime.rememberCoroutineScope
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
import br.com.gabryel.reginaesanguine.app.ui.components.InstanceNavigationStack
import br.com.gabryel.reginaesanguine.app.ui.theme.GridCheckeredOn
import br.com.gabryel.reginaesanguine.app.util.Mode
import br.com.gabryel.reginaesanguine.app.util.Mode.LOCAL
import br.com.gabryel.reginaesanguine.app.util.Mode.REMOTE
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens.DECK_SELECTION
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens.GAME
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens.HOME
import br.com.gabryel.reginaesanguine.app.util.getStandardPack
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckEditViewModel
import br.com.gabryel.reginaesanguine.viewmodel.deck.LocalDeckViewModel
import br.com.gabryel.reginaesanguine.viewmodel.deck.RemoteDeckViewModel
import br.com.gabryel.reginaesanguine.viewmodel.deck.SingleDeckViewModel
import br.com.gabryel.reginaesanguine.viewmodel.game.GameViewModel
import br.com.gabryel.reginaesanguine.viewmodel.game.remote.LocalGameClient
import coil3.SingletonImageLoader
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import kotlinx.coroutines.CoroutineScope

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
    var mode by remember { mutableStateOf(LOCAL) }

    context(mode) {
        val deckViewModel = createDeckViewModel(pack)
        val background = painterLoader.loadStaticImage(Res.drawable.static_temp_fandom_bgblur)

        Image(background, null, Modifier.fillMaxSize(), TopCenter, FillWidth)

        Surface(Modifier.fillMaxSize(), color = GridCheckeredOn.copy(alpha = 0.6f)) {
            InstanceNavigationStack(HOME) {
                addRoute(HOME) {
                    HomeScreen { mode = it }
                }
                addRoute(DECK_SELECTION) {
                    DeckSelectionScreen(deckViewModel)
                }
                addRoute(GAME) {
                    val coroutineScope = rememberCoroutineScope()
                    val game by produceState<GameViewModel?>(null) { value = createViewModel(deckViewModel, pack, coroutineScope) }

                    game?.let {
                        GameScreen(it)
                    }
                }
            }
        }
    }
}

context(mode: Mode)
private fun createDeckViewModel(pack: Pack): DeckEditViewModel = when (mode) {
    LOCAL -> LocalDeckViewModel(SingleDeckViewModel(pack), SingleDeckViewModel(pack))
    REMOTE -> RemoteDeckViewModel(SingleDeckViewModel(pack))
}

private suspend fun createViewModel(deckViewModel: DeckEditViewModel, pack: Pack, coroutineScope: CoroutineScope): GameViewModel =
    when (deckViewModel) {
        is LocalDeckViewModel -> {
            val leftDeck = deckViewModel.leftPlayer.viewDecks.value.selectedDeck
            val rightDeck = deckViewModel.rightPlayer.viewDecks.value.selectedDeck

            val leftPlayer = Player(emptyList(), leftDeck.shuffled())
            val rightPlayer = Player(emptyList(), rightDeck.shuffled())
            val game = Game.forPlayers(leftPlayer, rightPlayer)

            GameViewModel.forLocalGame(game, coroutineScope)
        }
        is RemoteDeckViewModel -> {
            val leftDeck = deckViewModel.leftPlayer.viewDecks.value.selectedDeck
            val client = LocalGameClient(400)

            GameViewModel.forRemoteGame(pack, leftDeck.shuffled(), LEFT, client, coroutineScope)
        }
    }
