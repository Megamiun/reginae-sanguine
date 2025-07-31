package br.com.gabryel.reginaesanguine.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import br.com.gabryel.reginaesanguine.app.services.ResResourceLoader
import br.com.gabryel.reginaesanguine.app.ui.GameBoard
import br.com.gabryel.reginaesanguine.app.ui.theme.ReginaeSanguineTheme
import br.com.gabryel.reginaesanguine.app.util.createRandomDeckOfSize
import br.com.gabryel.reginaesanguine.domain.Board
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.atColumn
import kotlin.system.exitProcess

fun main() = application {
    val deck = createRandomDeckOfSize(20)

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

    Window(title = "Reginae Sanguine", onCloseRequest = { exitProcess(0) }) {
        ReginaeSanguineTheme {
            Scaffold { paddingValues ->
                Box(
                    Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    GameBoard(game, ResResourceLoader())
                }
            }
        }
    }
}
