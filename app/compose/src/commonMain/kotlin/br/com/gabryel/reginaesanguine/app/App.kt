package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LocalRippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import br.com.gabryel.reginaesanguine.app.services.CardImageLoader
import br.com.gabryel.reginaesanguine.app.services.ResourceLoader
import br.com.gabryel.reginaesanguine.app.ui.NavigationScreens.GAME
import br.com.gabryel.reginaesanguine.app.ui.NavigationScreens.HOME
import br.com.gabryel.reginaesanguine.app.ui.theme.PurpleLight
import br.com.gabryel.reginaesanguine.app.util.createTestDeck
import br.com.gabryel.reginaesanguine.domain.Board
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.atColumn
import br.com.gabryel.reginaesanguine.viewmodel.GameViewModel

@Composable
context(_: CardImageLoader)
fun App(resourceLoader: ResourceLoader) {
    val gameViewModel = remember { createViewModel(resourceLoader) }

    CompositionLocalProvider(LocalRippleConfiguration provides null) {
        NavigationStack(HOME) {
            addRoute(HOME) {
                HomeScreen()
            }
            addRoute(GAME) {
                Box(Modifier.fillMaxSize().background(PurpleLight), contentAlignment = Center) {
                    GameBoard(gameViewModel)
                }
            }
        }
    }
}

private fun createViewModel(resourceLoader: ResourceLoader): GameViewModel {
    val deck = createTestDeck(resourceLoader)
    val knownCard = Card("001", "Security Officer", 1, 1, setOf())
    val unknownCard = Card("Custom", "Custom", 3, 3, setOf())
    val board = Board(
        mapOf(
            (0 atColumn 0) to Cell(LEFT, 1, knownCard),
            (1 atColumn 0) to Cell(LEFT, 2),
            (2 atColumn 0) to Cell(LEFT, 3, unknownCard),
            (0 atColumn 4) to Cell(RIGHT, 1, knownCard),
            (1 atColumn 4) to Cell(RIGHT, 2),
            (2 atColumn 4) to Cell(RIGHT, 3),
        ),
    )

    val game = Game.forPlayers(Player(deck = deck.shuffled()), Player(deck = deck.shuffled()))
    return GameViewModel.forGame(game.copy(board = board))
}
