package br.com.gabryel.reginaesanguine

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.Result
import br.com.gabryel.reginaesanguine.domain.Success
import br.com.gabryel.reginaesanguine.domain.column
import br.com.gabryel.reginaesanguine.domain.lane
import br.com.gabryel.reginaesanguine.ui.theme.ReginaeSanguineTheme
import kotlin.math.floor
import kotlin.math.log
import kotlin.random.Random.Default.nextDouble
import kotlin.random.Random.Default.nextInt

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE

        val deck = createRandomDeckOfSize(20)

        val game = Game.forPlayers(
            Player(deck = deck.shuffled()),
            Player(deck = deck.shuffled()),
        )
        val player = game.players[game.nextPlayer]
            ?: throw IllegalStateException("Couldn´t find player ${game.nextPlayer}")

        val area = game.height to game.width

        enableEdgeToEdge()
        setContent {
            ReginaeSanguineTheme {
                Scaffold { padding ->
                    Column(modifier = Modifier.padding(padding)) {
                        Text("Round ${game.round}")
                        Spacer(Modifier.size(30f.dp))
                        Row {
                            Grid(IntSize(1, 3), showBorder = false) { position ->
                                Box(modifier = Modifier.matchParentSize(), contentAlignment = Center) {
                                    Text(game.getLaneScore(position.lane())[LEFT]?.let { "⚡ $it" }.orEmpty())
                                }
                            }
                            Grid(IntSize(game.width, game.height)) { position ->
                                Box(modifier = Modifier.matchParentSize(), contentAlignment = Center) {
                                    val cellContent = game.getCellAt(position)

                                    Column(
                                        modifier = Modifier.matchParentSize(),
                                        horizontalAlignment = CenterHorizontally,
                                    ) {
                                        Text(position.describePosition())
                                        Text(cellContent.describeOwner().orEmpty())
                                        Text(cellContent.describeCard().orEmpty())
                                    }
                                }
                            }
                            Grid(IntSize(1, 3), showBorder = false) { position ->
                                Box(modifier = Modifier.matchParentSize(), contentAlignment = Center) {
                                    Text(game.getLaneScore(position.lane())[RIGHT]?.let { "⚡ $it" }.orEmpty())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Grid(gridSize: IntSize, showBorder: Boolean = true, getCell: @Composable BoxScope.(Position) -> Unit) {
    val modifier = Modifier
        .size(DpSize(100f.dp, 70f.dp))
        .border(1f.dp, if (showBorder) Color.Black else Color.Unspecified)

    FlowRow(maxItemsInEachRow = gridSize.width, maxLines = gridSize.height) {
        repeat(gridSize.height) { row ->
            repeat(gridSize.width) { col ->
                Box(
                    modifier = modifier,
                ) {
                    getCell(row to col)
                }
            }
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

fun Card.describe(): String = "$name (⚡ $rank, P $power) - $increments"

private fun Position.describePosition() = "${lane()}-${column()}"

private fun Result<Cell>.describeOwner() = (this as? Success<Cell>)?.value
    ?.owner?.name

private fun Result<Cell>.describeCard() = (this as? Success<Cell>)?.value
    ?.takeIf { it.owner != null }
    ?.run {
        listOfNotNull(
            totalPower?.let { "P $it" },
            "⚡ $rank",
        ).joinToString(" ")
    }
