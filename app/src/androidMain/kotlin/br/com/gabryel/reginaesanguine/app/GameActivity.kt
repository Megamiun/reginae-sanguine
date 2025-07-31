package br.com.gabryel.reginaesanguine.app

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
import androidx.core.view.WindowCompat.getInsetsController
import androidx.core.view.WindowInsetsCompat.Type.systemBars
import br.com.gabryel.reginaesanguine.app.services.AssetsResourceLoader
import br.com.gabryel.reginaesanguine.app.services.ResCardImageLoader
import br.com.gabryel.reginaesanguine.app.ui.GameBoard
import br.com.gabryel.reginaesanguine.app.ui.theme.PurpleLight
import br.com.gabryel.reginaesanguine.app.ui.theme.ReginaeSanguineTheme
import br.com.gabryel.reginaesanguine.app.util.createTestDeck
import br.com.gabryel.reginaesanguine.domain.Board
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.atColumn

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        val deck = createTestDeck(AssetsResourceLoader(this))

        val knownCard = Card("001", "Security Officer", setOf(), 1, 1)
        val unknownCard = Card("Custom", "Custom", setOf(), 3, 3)

        val game = Game.forPlayers(
            Player(deck = deck.shuffled()),
            Player(deck = deck.shuffled()),
        ).copy(
            board = Board(
                mapOf(
                    (0 atColumn 0) to Cell(LEFT, 1, knownCard),
                    (1 atColumn 0) to Cell(LEFT, 2),
                    (2 atColumn 0) to Cell(LEFT, 3, unknownCard),
                    (0 atColumn 4) to Cell(RIGHT, 1, knownCard),
                    (1 atColumn 4) to Cell(RIGHT, 2),
                    (2 atColumn 4) to Cell(RIGHT, 3, unknownCard),
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
                    GameBoard(game, ResCardImageLoader())
                }
            }
        }
    }
}
