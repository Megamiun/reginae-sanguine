package br.com.gabryel.reginaesanguine

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.CompositionLocalContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import br.com.gabryel.desktop.generated.resources.Res
import br.com.gabryel.reginaesanguine.domain.*
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.services.ResourceLoader
import br.com.gabryel.reginaesanguine.ui.GameBoard
import br.com.gabryel.reginaesanguine.ui.theme.ReginaeSanguineTheme
import coil3.BitmapImage
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import kotlinx.coroutines.launch
import org.jetbrains.skia.skottie.LogLevel
import org.jetbrains.skia.skottie.LogLevel.ERROR
import java.io.File
import java.util.logging.Level
import java.util.logging.Level.INFO
import java.util.logging.Level.WARNING
import java.util.logging.Logger
import kotlin.coroutines.coroutineContext
import kotlin.math.floor
import kotlin.math.log
import kotlin.random.Random.Default.nextDouble
import kotlin.random.Random.Default.nextInt
import kotlin.system.exitProcess

fun main() = application {
    val deck = createRandomDeckOfSize(20)

    val knownCard = Card("001", mapOf(), 1, 1)
    val unknownCard = Card("Art Missing", mapOf(), 3, 3)

    val game = Game.forPlayers(
        Player(deck = deck.shuffled()),
        Player(deck = deck.shuffled()),
    ).copy(
        board = Board(
            mapOf(
                (0 to 0) to Cell(LEFT, 1, knownCard),
                (1 to 0) to Cell(LEFT, 2),
                (2 to 0) to Cell(LEFT, 3, unknownCard),
                (0 to 4) to Cell(RIGHT, 1, knownCard),
                (1 to 4) to Cell(RIGHT, 2),
                (2 to 4) to Cell(RIGHT, 3, unknownCard),
            ),
        ),
    )

    Window(title = "Reginae Sanguine", onCloseRequest = { exitProcess(0) }) {
        ReginaeSanguineTheme {
            Scaffold { paddingValues ->
                Box(Modifier.padding(paddingValues)) {
                    GameBoard(game, Main.getResourceLoader())
                }
            }
        }
    }
}

class Main {
    companion object {
        private val logger = Logger.getLogger(Main::class.simpleName)

        fun getResourceLoader() = ResourceLoader { pack, player, id ->
            val color = when (player) {
                LEFT -> "blue"
                RIGHT -> "red"
            }

            val path = "drawable/${pack}_${color}_$id.png"
            runCatching {
                val uri = Res.getUri(path)
                rememberAsyncImagePainter(
                    uri,
                    onError = { logger.log(WARNING, "Error ${it.result.throwable.message}") },
                )
            }.onFailure {
                logger.warning("Couldn't load card at $path")
            }.getOrNull()
        }
    }
}

private fun createRandomDeckOfSize(cards: Int): List<Card> = (1..cards).map {
    val increments = (0..1 + nextInt(4)).map {
        (nextInt(-1, 2) to nextInt(-1, 2)) to 1
    }.distinct().toMap()

    Card(
        "Test Card $it",
        increments,
        nextInt(1, 4),
        3 - floor(log(nextDouble(1.0, 250.0), 10.0)).toInt(),
    )
}
