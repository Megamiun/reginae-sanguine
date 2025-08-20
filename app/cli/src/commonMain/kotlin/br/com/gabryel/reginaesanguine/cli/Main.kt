package br.com.gabryel.reginaesanguine.cli

import androidx.compose.runtime.LaunchedEffect
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Displacement
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.viewmodel.game.GameViewModel
import com.jakewharton.mosaic.runMosaicMain
import kotlinx.coroutines.awaitCancellation
import kotlin.math.floor
import kotlin.math.log
import kotlin.random.Random.Default.nextDouble
import kotlin.random.Random.Default.nextInt

fun main() = runMosaicMain {
    val deck = createRandomDeckOfSize(20)
    val startGame = Game.forPlayers(
        Player(deck = deck.shuffled()),
        Player(deck = deck.shuffled()),
    )

    GameApp(GameViewModel.forGame(startGame))
    LaunchedEffect(Unit) {
        awaitCancellation()
    }
}

private fun createRandomDeckOfSize(cards: Int): List<Card> = (1..cards).map {
    val increments = (0..1 + nextInt(4)).map {
        Displacement(nextInt(-1, 2), nextInt(-1, 2))
    }.toSet()

    Card(
        "$it",
        "Test Card $it",
        nextInt(1, 4),
        3 - floor(log(nextDouble(1.0, 250.0), 10.0)).toInt(),
        increments,
    )
}
