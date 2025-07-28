package br.com.gabryel.reginaesanguine.ui

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat.getInsetsController
import androidx.core.view.WindowInsetsCompat.Type.systemBars
import br.com.gabryel.reginaesanguine.GameBoard
import br.com.gabryel.reginaesanguine.domain.Board
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.services.ResourceLoader
import br.com.gabryel.reginaesanguine.ui.theme.PurpleLight
import br.com.gabryel.reginaesanguine.ui.theme.ReginaeSanguineTheme
import java.util.logging.Logger
import kotlin.math.floor
import kotlin.math.log
import kotlin.random.Random.Default.nextDouble
import kotlin.random.Random.Default.nextInt

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = SCREEN_ORIENTATION_SENSOR_LANDSCAPE

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

        getInsetsController(window, window.decorView).hide(systemBars())

        enableEdgeToEdge()
        setContent {
            ReginaeSanguineTheme {
                Box(
                    modifier = Modifier.fillMaxSize().background(PurpleLight),
                    contentAlignment = Center,
                ) {
                    GameBoard(game, getResourceLoader())
                }
            }
        }
    }

    // TODO Find a better way to load images dynamically
    private fun getResourceLoader() = ResourceLoader { pack, player, id ->
        val color = when (player) {
            LEFT -> "blue"
            RIGHT -> "red"
        }

        val drawable = "${pack}_${color}_$id"
        Logger.getLogger("ResourceLoader").info(drawable)

        val id = resources.getIdentifier(drawable, "drawable", packageName)

        if (id != 0) painterResource(id) else null
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
