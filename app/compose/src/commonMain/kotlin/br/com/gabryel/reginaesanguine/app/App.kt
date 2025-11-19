package br.com.gabryel.reginaesanguine.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
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
import br.com.gabryel.reginaesanguine.app.util.getStandardPackFromServer
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.logging.Logger
import br.com.gabryel.reginaesanguine.server.client.KtorServerClient
import br.com.gabryel.reginaesanguine.viewmodel.auth.AuthState
import br.com.gabryel.reginaesanguine.viewmodel.auth.AuthViewModel
import br.com.gabryel.reginaesanguine.viewmodel.auth.Storage
import br.com.gabryel.reginaesanguine.viewmodel.auth.remote.RemoteAuthClient
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckEditViewModel
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckViewModel
import br.com.gabryel.reginaesanguine.viewmodel.deck.DualDeckEditViewModel
import br.com.gabryel.reginaesanguine.viewmodel.deck.SingleDeckEditViewModel
import br.com.gabryel.reginaesanguine.viewmodel.deck.local.LocalDeckManager
import br.com.gabryel.reginaesanguine.viewmodel.deck.remote.RemoteDeckClient
import br.com.gabryel.reginaesanguine.viewmodel.deck.remote.RemoteDeckManager
import br.com.gabryel.reginaesanguine.viewmodel.game.GameClient
import br.com.gabryel.reginaesanguine.viewmodel.game.GameViewModel
import br.com.gabryel.reginaesanguine.viewmodel.game.remote.RemoteGameClient
import br.com.gabryel.reginaesanguine.viewmodel.pack.remote.RemotePackClient
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.compose.LocalPlatformContext
import coil3.request.Disposable
import coil3.request.ImageRequest
import kotlinx.coroutines.CoroutineScope

@Composable
context(painterLoader: PainterLoader)
fun App(resourceLoader: ResourceLoader, storage: Storage) {
    val logger = Logger("App")
    val context = LocalPlatformContext.current
    val coroutineScope = rememberCoroutineScope()

    var loadingImages by remember { mutableStateOf(false) }
    var loadingPack by remember { mutableStateOf(true) }
    var pack by remember { mutableStateOf<Pack?>(null) }
    var mode by remember { mutableStateOf(LOCAL) }
    val snackbar = remember { SnackbarHostState() }

    val baseUrl = remember { storage.serverUrl.retrieve() ?: "http://10.0.2.2:8080" }
    val serverClient = remember { KtorServerClient(baseUrl) }
    val authClient = remember { RemoteAuthClient(serverClient) }
    val packClient = remember { RemotePackClient(serverClient) }
    val gameClient = remember { RemoteGameClient(serverClient) }
    val authViewModel = remember { AuthViewModel(authClient, storage, coroutineScope) }

    LaunchedEffect(true) {
        logger.info("Loading images")
        val startupResources = listOf("static_temp_fandom_bgblur", "static_temp_boardgamegeek_logo")
            .map { preload(context, it) }

        startupResources.forEach { it.job.await() }

        val resources = Res.allDrawableResources.map { (key) -> preload(context, key) }
        resources.forEach { it.job.await() }

        loadingImages = false
    }

    LaunchedEffect(mode) {
        logger.info("Start loading pack = $mode")
        loadingPack = true
        when (mode) {
            LOCAL -> pack = getStandardPack(resourceLoader)
            REMOTE -> try {
                pack = getStandardPackFromServer(packClient)
            } catch (e: Exception) {
                logger.error("Failed to load pack from server. Switching to local.", e)
                // TODO Stop waiting for snackbar to continue
                snackbar.showSnackbar(
                    "Failed to load pack from server. Switching to local.",
                    withDismissAction = true,
                )
                mode = LOCAL
            }
        }
        loadingPack = false
        logger.info("End loading pack = $mode")
    }

    val currentPack = pack ?: return

    context(mode) {
        val authState = authViewModel.state.collectAsState().value
        val token = (authState as? AuthState.Authenticated)?.token
        val deckViewModel = createDeckViewModel(currentPack, serverClient, token, coroutineScope)
        val background = painterLoader.loadStaticImage(Res.drawable.static_temp_fandom_bgblur)

        Image(background, null, Modifier.fillMaxSize(), TopCenter, FillWidth)

        Surface(Modifier.fillMaxSize(), color = GridCheckeredOn.copy(alpha = 0.6f)) {
            InstanceNavigationStack(HOME) {
                addRoute(HOME) {
                    HomeScreen(authViewModel) { newMode -> mode = newMode }
                }
                addRoute(DECK_SELECTION) {
                    DeckSelectionScreen(deckViewModel)
                }
                addRoute(GAME) {
                    val coroutineScope = rememberCoroutineScope()
                    val game by produceState<GameViewModel?>(null) {
                        value = createViewModel(deckViewModel, currentPack, gameClient, coroutineScope)
                    }

                    game?.let {
                        GameScreen(it)
                    }
                }
            }
        }
    }

    if (loadingImages || loadingPack) {
        Surface(Modifier.fillMaxSize(), color = Black.copy(alpha = 0.5f)) {
            Box(Modifier.fillMaxSize(), contentAlignment = Center) {
                CircularProgressIndicator(modifier = Modifier.width(64.dp))
            }
        }
    }

    SnackbarHost(
        hostState = snackbar,
        modifier = Modifier.padding(horizontal = 16.dp),
        snackbar = { data ->
            Snackbar(
                containerColor = Red,
                contentColor = White,
                dismissAction = {
                    IconButton(onClick = data::dismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss")
                    }
                },
            ) {
                Text(data.visuals.message)
            }
        },
    )
}

private fun preload(context: PlatformContext, key: String): Disposable {
    val request = ImageRequest.Builder(context)
        .data(Res.getUri("drawable/$key.png"))
        .build()
    return SingletonImageLoader.get(context).enqueue(request)
}

context(mode: Mode)
private fun createDeckViewModel(
    pack: Pack,
    serverClient: KtorServerClient,
    token: String?,
    coroutineScope: CoroutineScope
): DeckEditViewModel = when (mode) {
    LOCAL -> {
        val leftManager = LocalDeckManager(pack)
        val rightManager = LocalDeckManager(pack)
        DualDeckEditViewModel(
            DeckViewModel(pack, leftManager, coroutineScope),
            DeckViewModel(pack, rightManager, coroutineScope),
        )
    }

    REMOTE -> {
        val authToken = token ?: error("Token required for remote mode")
        val deckClient = RemoteDeckClient(serverClient, authToken)
        val deckManager = RemoteDeckManager(deckClient, coroutineScope, pack)
        val deckViewModel = DeckViewModel(pack, deckManager, coroutineScope)
        deckViewModel.refresh()
        SingleDeckEditViewModel(deckViewModel, deckManager = deckManager)
    }
}

private suspend fun createViewModel(
    deckViewModel: DeckEditViewModel,
    pack: Pack,
    gameClient: GameClient,
    coroutineScope: CoroutineScope
): GameViewModel =
    when (deckViewModel) {
        is DualDeckEditViewModel -> {
            val leftDeck = deckViewModel.leftPlayer.viewDecks.value.selectedDeck
            val rightDeck = deckViewModel.rightPlayer.viewDecks.value.selectedDeck

            val leftPlayer = Player(emptyList(), leftDeck.shuffled())
            val rightPlayer = Player(emptyList(), rightDeck.shuffled())
            val game = Game.forPlayers(leftPlayer, rightPlayer)

            GameViewModel.forLocalGame(game, coroutineScope)
        }

        is SingleDeckEditViewModel -> {
            val deckStateId = deckViewModel.getSelectedDeckStateId()
                ?: error("No deck selected for remote game")

            GameViewModel.forRemoteGame(deckStateId, LEFT, gameClient, coroutineScope, pack.cards.associateBy { it.id })
        }
    }
